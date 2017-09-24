package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Phase;

import java.beans.EventHandler;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    private String login = "login";
    private  String password = "123456";
    private String address = "address";

    private BlockchainConnector connector = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
       authWindow(primaryStage);
    }

    private void authWindow(Stage primaryStage){
        VBox root = new VBox();
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(false);

        Label loginLbl = new Label("Account login: ");
        Label pswLbl = new Label("Account password: ");
        Label addrsLbl = new Label("Contract address: ");

        TextField loginTF = new TextField();
        PasswordField pswTF = new PasswordField();
        TextField addrsTF = new TextField();

        gridPane.addColumn(0, loginLbl, pswLbl, addrsLbl);
        gridPane.addColumn(1, loginTF, pswTF, addrsTF);
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPrefWidth(130);
        ColumnConstraints constraints2 = new ColumnConstraints();
        constraints2.setPrefWidth(220);
        gridPane.getColumnConstraints().addAll(constraints, constraints2);

        Button submit = new Button("submit");
//        submit.setBackground(getBackground(Color.color(0.2, 0.7, 0.5, 0.1)));
//        submit.setPadding(getInsets(15,15,15,15));
        submit.setAlignment(Pos.CENTER);
        submit.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!loginTF.getText().isEmpty() && !loginTF.getText().isEmpty() && !loginTF.getText().isEmpty()) {
                    login = loginTF.getText();
                    password = pswTF.getText();
                    address = addrsTF.getText();
//                    if (connector == null)
                        connector = new BlockchainConnector(password,
                                "/Users/victoria/IdeaProjects/GeoApps/icolabhack/src/main/resources/UTC--2017-09-23T09-59-58.770000000Z--41b85c73a60830e40e0a4b5d1bffe5deff6ae919.json");
//                        connector = new BlockchainConnector("70b2a9422b2e990ca0add24f06faacb9d35065b23e5c9cb5f56470917fb8ca65",
//                                null); //TODO: DELETE HARDCODE
                    if (connector.checkValidData(loginTF.getText(), pswTF.getText(), addrsTF.getText()))
                        startMainWindow(primaryStage);
                }
            }
        });
        gridPane.setPadding(getInsets(0,0,0,10));
        root.getChildren().addAll(gridPane, submit);
        root.setAlignment(Pos.CENTER);
        root.setPadding(getInsets(0,0,10,10));
        primaryStage.setScene(new Scene(root, 350,130));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void startMainWindow(Stage primaryStage){
        VBox root = new VBox();//FXMLLoader.load(getClass().getResource("sample.fxml"));
        VBox header = new VBox();
        Label accountLbl = new Label("Account: " + login);
        accountLbl.setFont(Font.font(18));
        Label addressLbl = new Label("Address: " + address);
        addressLbl.setFont(Font.font(18));
        Double balance = null;
        try {
             balance = connector.getBalance();
        } catch (Exception e){
            e.printStackTrace();
        }
        Label balanceLbl = new Label("Balance: ");
        balanceLbl.setFont(Font.font(18));
        if (balance != null)
            balanceLbl.setText(balanceLbl.getText().concat(balance.toString()));
        else
            System.out.println("Some error while receiving balance");
        HBox hBox = new HBox();

        VBox history = getHistoryPane(connector.getPhases(address, "getPhases"));
        VBox invoicesBox = getInvoicePane();

        hBox.getChildren().addAll(invoicesBox, history);
        invoicesBox.setMaxWidth(Double.MAX_VALUE);
        history.setMaxWidth(Double.MAX_VALUE);

        hBox.setHgrow(history, Priority.ALWAYS);
        hBox.setHgrow(invoicesBox, Priority.ALWAYS);

        header.getChildren().addAll(accountLbl, addressLbl, balanceLbl);
        header.setBorder(getBorder());
        header.setPadding(getInsets(15,15,15,15));
        header.setBackground(getBackground(Color.rgb(100, 200, 230, 0.1)));//Color.LIGHTSKYBLUE));


        AnchorPane anchorPane = new AnchorPane();
        Button back = new Button("back");
        anchorPane.getChildren().add(back);
        AnchorPane.setLeftAnchor(back, 15d);
        AnchorPane.setTopAnchor(back, 5d);
        back.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                authWindow(primaryStage);
            }
        });

        root.getChildren().addAll(header, hBox, anchorPane);
        primaryStage.setTitle("ICO");
//        primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
        primaryStage.setResizable(true);
    }


    private VBox getHistoryPane(List<Phase> phases){
        VBox historyBox = new VBox();
        VBox vFramesBox = new VBox();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);

        VBox lblBox = new VBox();
        lblBox.setBackground(getBackground(Color.LIGHTGRAY));

        Label historyLbl = new Label("History");
        historyLbl.setFont(Font.font(20));
        historyLbl.setAlignment(Pos.CENTER);
        historyLbl.setTextAlignment(TextAlignment.CENTER);
        historyLbl.setPrefSize(150, 30);
        lblBox.getChildren().add(historyLbl);

        historyBox.setBorder(getBorder());
        historyBox.setFillWidth(true);

        // Adding elements
        historyBox.getChildren().add(lblBox);
        int size = phases.size();
        // Sorting for newest being on the top
        for(int i=0; i < size; i++){
            Phase phase = phases.get(i);
            if ((i != 0) && !phase.isFinished()) {
                for (int j = i-1; j >= 0; j--) {
                    Phase shiftingPhase = phases.get(j);
                    phases.add(j + 1, shiftingPhase);
                }
                phases.add(0, phase);
            }
        }

        for (int i=0; i < size; i++) {
//            if (phases.get(0).isFinished())
            vFramesBox.getChildren().add(drawHistoryInvoice(phases.get(i)));
        }

        scrollPane.setContent(vFramesBox);
        historyBox.getChildren().add(vFramesBox);
        return historyBox;
    }

    private VBox getInvoicePane(){
        VBox invoicesBox = new VBox();
        VBox lblBox = new VBox();
        lblBox.setBackground(getBackground(Color.LIGHTGRAY));

        Label invoicesLbl = new Label("Invoices");
        invoicesLbl.setFont(Font.font(20));
        invoicesLbl.setPrefSize(150, 30);
        invoicesLbl.setTextAlignment(TextAlignment.CENTER);
        invoicesLbl.setAlignment(Pos.BASELINE_CENTER);
        lblBox.getChildren().add(invoicesLbl);

        invoicesBox.setFillWidth(true);
        invoicesBox.getChildren().add(lblBox);
        invoicesBox.getChildren().add(drawInvoice("First", 100, 100));
        invoicesBox.setBorder(getBorder());
        return invoicesBox;
    }

    private VBox drawHistoryInvoice(Phase phase){
        VBox root = new VBox();
        VBox localRoot = new VBox();
        String title = phase.getName();
        Label titleLbl = new Label(title);
//        Label description = new Label("Some description ...");
        titleLbl.setPrefSize(150, 30);
        titleLbl.setTextAlignment(TextAlignment.CENTER);
        titleLbl.setAlignment(Pos.CENTER);

        if (!phase.isFinished()) {
            HBox hBox = new HBox();
            List<Button> buttons = new ArrayList<Button>();
            Button bad = new Button("bad");
            Button well = new Button("well");
            Button good = new Button("good");
            bad.setBackground(getBackground(Color.color(1.0, 0.0, 0.0, 0.3)));
            well.setBackground(getBackground(Color.color(1.0,1.0,0.0, 0.3)));
            good.setBackground(getBackground(Color.color(0.0, 1.0, 0.8, 0.3)));
            buttons.add(bad);
            buttons.add(well);
            buttons.add(good);
            buttons.forEach((b) -> b.setOnAction(this::sendAssessment));
            hBox.getChildren().addAll(buttons);
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER);

            localRoot.getChildren().addAll(titleLbl, hBox/*, description*/);
            localRoot.setBackground(getBackground(Color.rgb(100, 200, 100, 0.1)));
        } else {
            Label descLbl = new Label(phase.getDescription());
//        Label description = new Label("Some description ...");
            descLbl.setPrefSize(150, 30);
            descLbl.setTextAlignment(TextAlignment.CENTER);
            descLbl.setAlignment(Pos.CENTER);
            localRoot.getChildren().addAll(titleLbl, descLbl);
            localRoot.setBackground(getBackground(Color.LIGHTGRAY));//Color.rgb(100, 200, 230, 0.1)));
        }

        localRoot.setPadding(getInsets(15,15,15,15));
        localRoot.setBorder(getBorder());
        localRoot.setAlignment(Pos.CENTER);


        root.getChildren().add(localRoot);
        root.setPadding(getInsets(30,30,30,30));
        return root;
    }

    private Node drawInvoice(String title, double width, double height){
        VBox root = new VBox();
        VBox localRoot = new VBox();
//        AnchorPane localRoot = new AnchorPane();
        Label titleLbl = new Label(title);
//        titleLbl.setBackground(getBackground(Color.));
        titleLbl.setPrefSize(150, 30);
        titleLbl.setTextAlignment(TextAlignment.CENTER);
        Label description = new Label("Some description ...");
        titleLbl.setAlignment(Pos.BASELINE_CENTER);
        description.setAlignment(Pos.BASELINE_CENTER);

        HBox hBox = new HBox();
        List<Button> buttons = new ArrayList<Button>();

        Button accept = new Button("accept");
        Button reject = new Button("reject");
        accept.setOnAction(this::sendAccept);
        reject.setOnAction(this::sendReject);

        buttons.add(accept);
        buttons.add(reject);

        hBox.getChildren().addAll(buttons);

        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        localRoot.setPadding(getInsets(15,15,15,15));
        localRoot.getChildren().addAll(titleLbl, description, hBox);
        localRoot.setBorder(getBorder());
        localRoot.setAlignment(Pos.CENTER);
        localRoot.setBackground(getBackground(Color.rgb(100, 200, 100, 0.1)));
        root.getChildren().add(localRoot);
//        localRoot.setSpacing(30);
//        localRoot.setPadding(getInsets(15, 15, 15, 15));
        root.setPadding(getInsets(30,30,30,30));

        return root;
    }

    private void sendAccept(ActionEvent event) {
        connector.sendAccept();
    }

    private void sendReject(ActionEvent event) {
        connector.sendReject();
    }

    private void sendAssessment(ActionEvent event){
        String value = ((Button) event.getSource()).getText();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Border getBorder(){
        return new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderStroke.THIN));
    }

    private Background getBackground(Color color){
        return new Background(new BackgroundFill(color, null, null));
    }
    private Insets getInsets(double l, double r, double t, double b){
        return new Insets(t, r, b, l);
    }
}
