package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Phase;
import org.web3j.crypto.CipherException;
import sun.security.jgss.GSSCaller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private TextField loginTF = new TextField();

    private String login = "login";
    private  String password = "123456";
    private String contractAddress = "contractAddress";
    private String secretFilePath = "/Users/victoria/IdeaProjects/icolab_client/src/main/resources/secret.json";
    private File file;
    private SecretCLass secretData;

    private BlockchainConnector connector = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
       authWindow(primaryStage);
    }

    private void authWindow(Stage primaryStage){
        VBox root = new VBox();
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(false);

        Label loginLbl = new Label("Account login:   ");
        Label pswLbl = new Label("Account password:    ");
        Label addrsLbl = new Label("Contract contractAddress:   ");
        loginLbl.setPadding(getInsets(3, 0, 0, 0));
        pswLbl.setPadding(getInsets(3,0,0,0));
        addrsLbl.setPadding(getInsets(3, 0, 0, 0));

        PasswordField pswTF = new PasswordField();
        TextField addrsTF = new TextField("0x395699a7e5a66f586d9debd06e4ddffbe57ffbad");

        gridPane.addColumn(0, loginLbl, pswLbl, addrsLbl);
        gridPane.addColumn(1, loginTF, pswTF, addrsTF);
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPrefWidth(140);
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
                    contractAddress = addrsTF.getText();
//                    if (connector == null)
                    try{
                        connector = new BlockchainConnector(password,
                                "/Users/victoria/IdeaProjects/GeoApps/icolabhack/src/main/resources/UTC--2017-09-23T09-59-58.770000000Z--41b85c73a60830e40e0a4b5d1bffe5deff6ae919.json");
//                        connector = new BlockchainConnector("70b2a9422b2e990ca0add24f06faacb9d35065b23e5c9cb5f56470917fb8ca65",
//                                null); //TODO: DELETE HARDCODE
//                    if (connector.checkValidData(loginTF.getText(), pswTF.getText(), addrsTF.getText()))
                        getGsonFromFile();
                        startMainWindow(primaryStage);
                    } catch (CipherException e){
                        new Alert(Alert.AlertType.ERROR,"Wrong password").show();
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        gridPane.setPadding(getInsets(0,0,0,10));
        root.getChildren().addAll(gridPane, submit);
        root.setAlignment(Pos.CENTER);
        root.setPadding(getInsets(0,0,10,10));
        primaryStage.setScene(new Scene(root, 360,130));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                saveToFile();
            }
        });
    }

    private void startMainWindow(Stage primaryStage){
        VBox root = new VBox();//FXMLLoader.load(getClass().getResource("sample.fxml"));
        VBox header = new VBox();
        Label accountLbl = new Label("Account: " + login);
        accountLbl.setFont(Font.font(18));
        Label addressLbl = new Label("Address: " + contractAddress);
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

        List<List<Phase>> allPhases = connector.getPhases(contractAddress, "getPhases");
        VBox invoicesBox = getInvoicePane(allPhases.get(0).get(0));
        VBox history = getHistoryPane(allPhases.get(1));

        hBox.getChildren().addAll(invoicesBox, history);
        invoicesBox.setMaxWidth(Double.MAX_VALUE);
        history.setMaxWidth(Double.MAX_VALUE);

        hBox.setHgrow(history, Priority.ALWAYS);
        hBox.setHgrow(invoicesBox, Priority.ALWAYS);

        header.getChildren().addAll(accountLbl, addressLbl, balanceLbl);
        header.setBorder(getBorder());
        header.setPadding(getInsets(30,15,15,15));
        header.setBackground(getBackground(Color.rgb(100, 200, 230, 0.1)));//Color.LIGHTSKYBLUE));


        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setBorder(getBorder());
//        anchorPane.setBackground(getBackground(Color.rgb(100, 200, 230, 0.1)));
        Button back = new Button("back");
        back.setPrefSize(50,30);
        back.setMaxWidth(Double.MAX_VALUE);
        anchorPane.getChildren().add(back);
        AnchorPane.setLeftAnchor(back, 30d);
        AnchorPane.setTopAnchor(back, 3d);
        AnchorPane.setBottomAnchor(back, 3d);
        back.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveToFile();
//                loginTF.setText(login);
                authWindow(primaryStage);
            }
        });

        root.getChildren().addAll(header, hBox, anchorPane);
        primaryStage.setTitle("ICO");
//        primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root, 600, 650));
        primaryStage.setY(50);
        primaryStage.show();
        primaryStage.setResizable(true);
    }

    private VBox getHistoryPane(List<Phase> phases){
        VBox historyBox = new VBox();
        VBox vFramesBox = new VBox();
        vFramesBox.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);

        VBox lblBox = new VBox();
        lblBox.setBackground(getBackground(Color.LIGHTGRAY));

        Label historyLbl = new Label("History");
        historyLbl.setFont(Font.font(20));
        historyLbl.setAlignment(Pos.CENTER);
        historyLbl.setTextAlignment(TextAlignment.CENTER);
        historyLbl.setPrefSize(150, 30);
        historyLbl.setMinSize(150, 30);
        lblBox.getChildren().add(historyLbl);

        historyBox.setBorder(getBorder());
        historyBox.setFillWidth(true);

        // Adding elements
        historyBox.getChildren().add(lblBox);
        int size = phases.size();
        // Sorting for newest being on the top
        for(int i=0; i < size; i++){
            Phase phase = phases.get(i);
            if ((i != 0) && !phase.isEstimated()) {
                for (int j = i-1; j >= 0; j--) {
                    Phase shiftingPhase = phases.get(j);
                    phases.remove(j);
                    phases.add(j + 1, shiftingPhase);
//                    if (j == 0) {
//                        phases.remove(i);
//                        phases.add(0, phase);
//                    }
                }
            }
        }

        for (int i=0; i < size; i++) {
//            if (phases.get(0).isFinished())
            vFramesBox.getChildren().add(drawHistoryInvoice(phases.get(i)));
        }

        scrollPane.setContent(vFramesBox);

        historyBox.getChildren().addAll(scrollPane, vFramesBox);//vFramesBox);
        return historyBox;
    }

    private VBox getInvoicePane(Phase phase){
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
        invoicesBox.getChildren().add(drawInvoice(phase));
        invoicesBox.setBorder(getBorder());
        return invoicesBox;
    }

    private VBox drawHistoryInvoice(Phase phase){
        VBox root = new VBox();
        VBox localRoot = new VBox();
        Pane lblpane = new Pane();

        lblpane.setBorder(getSpecBorder(BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE));
        String title = phase.getName();
        Label titleLbl = new Label(title);
        Label descLbl = new Label(phase.getDescription() + "\nPrice: " + phase.getPrice());
//        Label description = new Label("Some description ...");
        titleLbl.setPrefSize(150, 30);
        titleLbl.setMinSize(150, 30);
        titleLbl.setTextAlignment(TextAlignment.CENTER);
        titleLbl.setAlignment(Pos.CENTER);
        lblpane.getChildren().add(titleLbl);

        if (!phase.isEstimated() && !secretData.estimated.contains(phase.getUid())) {
            HBox hBox = new HBox();
            List<Button> buttons = new ArrayList<Button>();
            Button bad = new Button("bad");
            Button well = new Button("well");
            Button good = new Button("good");
            Button excel = new Button("excellent");
//            bad.setBackground(getBackground(Color.color(1.0, 0.0, 0.0, 0.3)));
//            well.setBackground(getBackground(Color.color(1.0,1.0,0.0, 0.3)));
//            good.setBackground(getBackground(Color.color(0.0, 1.0, 0.8, 0.3)));
            buttons.add(bad);
            buttons.add(well);
            buttons.add(good);
            buttons.add(excel);
            buttons.forEach((b) -> b.setOnAction(
                    new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String value = ((Button) event.getSource()).getText();
                    connector.sendAssessment(value);

                    if (secretData == null)
                        secretData = new SecretCLass();
                    secretData.setAddress(connector.getAddress());
                    secretData.estimated.add(phase.getUid());
//                    descLbl.setPrefSize(150, 60);
//                    descLbl.setTextAlignment(TextAlignment.CENTER);
//                    descLbl.setAlignment(Pos.CENTER);
//                    localRoot.getChildren().addAll(lblpane, descLbl);
                    localRoot.setBackground(getBackground(Color.rgb(10, 10, 10, 0.3)));
                    lblpane.setBackground(getBackground(Color.LIGHTGRAY));
                    buttons.forEach(b -> b.setDisable(true));

                }
            }));
            hBox.getChildren().addAll(buttons);
            hBox.setSpacing(10);
            hBox.setPadding(new Insets(10, 0, 5, 0));
            hBox.setAlignment(Pos.CENTER);

//            Label descLbl = new Label(phase.getDescription() + "\nPrice: " + phase.getPrice());

            descLbl.setPrefSize(150, 50);
            descLbl.setTextAlignment(TextAlignment.CENTER);
            descLbl.setAlignment(Pos.CENTER);
            localRoot.getChildren().addAll(lblpane, descLbl, hBox);
            localRoot.setBackground(getBackground(Color.rgb(100, 200, 100, 0.1)));
            lblpane.setBackground(getBackground(Color.LIGHTGREEN));

        } else {
//            Label descLbl = new Label(phase.getDescription() + "\nPrice: " + phase.getPrice());
            if (phase.isEstimated()) // if all tokenholders have estimated
                descLbl.setText(descLbl.getText() + "\nEstimation: " + phase.getEstimation());

            descLbl.setPrefSize(150, 60);
            descLbl.setTextAlignment(TextAlignment.CENTER);
            descLbl.setAlignment(Pos.CENTER);
            localRoot.getChildren().addAll(lblpane, descLbl);
            localRoot.setBackground(getBackground(Color.rgb(10, 10, 10, 0.3)));
            lblpane.setBackground(getBackground(Color.LIGHTGRAY));
        }

        localRoot.setPadding(getInsets(0,0,0,0));
        localRoot.setBorder(getBorder());
        localRoot.setAlignment(Pos.CENTER);

        root.getChildren().add(localRoot);
        root.setPadding(getInsets(30,30,30,30));
        return root;
    }

    private Node drawInvoice(Phase phase){
        VBox root = new VBox();
        VBox localRoot = new VBox();
//        AnchorPane localRoot = new AnchorPane();
        Pane lblpane = new Pane();
        lblpane.setBackground(getBackground(Color.LIGHTGREEN));
        lblpane.setBorder(getSpecBorder(BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE));
        String title = phase.getName();
        Label titleLbl = new Label(title);
//        titleLbl.setBackground(getBackground(Color.));
        titleLbl.setPrefSize(150, 30);
        titleLbl.setTextAlignment(TextAlignment.CENTER);
        titleLbl.setAlignment(Pos.BASELINE_CENTER);
        lblpane.getChildren().add(titleLbl);
        Label description = new Label(phase.getDescription() + "\nPrice: " + phase.getPrice());
        description.setAlignment(Pos.BASELINE_CENTER);

        HBox hBox = new HBox();
        List<Button> buttons = new ArrayList<Button>();

        Button accept = new Button("accept");
        Button reject = new Button("reject");
        if (secretData.accepted.contains(phase.getUid())){
            setWorkingOrRejectedState(titleLbl, lblpane, localRoot, accept, reject,
                    "In working!", Color.LIGHTGRAY, Color.rgb(200, 200, 200, 0.8));
        } else if (secretData.rejected.contains(phase.getUid())){
            setWorkingOrRejectedState(titleLbl, lblpane, localRoot, accept, reject,
                    "Rejected!", Color.LIGHTGRAY, Color.rgb(200, 200, 200, 0.8));
        } else{ // If new
            localRoot.setBackground(getBackground(Color.rgb(100, 200, 100, 0.1)));
        }

        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connector.sendAccept();
                if (secretData == null)
                    secretData = new SecretCLass();
                secretData.setAddress(connector.getAddress());
                secretData.accepted.add(phase.getUid());

                setWorkingOrRejectedState(titleLbl, lblpane, localRoot, accept, reject,
                        "In working!", Color.LIGHTGRAY, Color.rgb(200, 200, 200, 0.8));
//                titleLbl.setText("In working!");
//                lblpane.setBackground(getBackground(Color.LIGHTGRAY));
//                localRoot.setBackground(getBackground(Color.rgb(200, 200, 200, 0.8)));
//                accept.setDisable(true);
//                reject.setDisable(true);
            }
        });
        reject.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connector.sendReject();
                if (secretData == null)
                    secretData = new SecretCLass();
                secretData.setAddress(connector.getAddress());
                secretData.rejected.add(phase.getUid());
                setWorkingOrRejectedState(titleLbl, lblpane, localRoot, accept, reject,
                        "Rejected!", Color.LIGHTGRAY, Color.rgb(200, 200, 200, 0.8));

//                titleLbl.setText("Rejected!");
//                lblpane.setBackground(getBackground(Color.LIGHTGRAY));
//                localRoot.setBackground(getBackground(Color.rgb(200, 200, 200, 0.8)));
//                accept.setDisable(true);
//                reject.setDisable(true);
            }
        });

        buttons.add(accept);
        buttons.add(reject);

        hBox.getChildren().addAll(buttons);

        hBox.setSpacing(30);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 5, 5, 5));
//        localRoot.setPadding(getInsets(15,15,15,15));
        localRoot.getChildren().addAll(lblpane, description, hBox);
        localRoot.setBorder(getBorder());
        localRoot.setAlignment(Pos.CENTER);
        root.getChildren().add(localRoot);
//        localRoot.setSpacing(30);
//        localRoot.setPadding(getInsets(15, 15, 15, 15));
        root.setPadding(getInsets(30,30,30,30));

        return root;
    }

    private void setWorkingOrRejectedState(Label titleLbl, Pane lblpane, VBox localRoot, Button accept, Button reject,
                                    String textForLbl, Color paneColor, Color backColor){
        titleLbl.setText(textForLbl);
        lblpane.setBackground(getBackground(paneColor));
        localRoot.setBackground(getBackground(backColor));
        accept.setDisable(true);
        reject.setDisable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Border getBorder(){
        return new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderStroke.THIN));
    }

    private Border getSpecBorder(BorderStrokeStyle t, BorderStrokeStyle r, BorderStrokeStyle b, BorderStrokeStyle l){
        return new Border(new BorderStroke(Color.BLACK,Color.BLACK, Color.BLACK, Color.BLACK,
                t, r, b, l, CornerRadii.EMPTY, null, null));
    }

    private Background getBackground(Color color){
        return new Background(new BackgroundFill(color, null, null));
    }
    private Insets getInsets(double l, double r, double t, double b){
        return new Insets(t, r, b, l);
    }

    private void getGsonFromFile(){
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(secretFilePath));
            secretData = gson.fromJson(reader, SecretCLass.class);
            System.out.println(secretData);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(secretFilePath))){

            bufferedWriter.append(new GsonBuilder().create().toJson(secretData, SecretCLass.class).toString());

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private class SecretCLass{
        private String address;
        private ArrayList<Integer> accepted = new ArrayList<>();
        private ArrayList<Integer> rejected=  new ArrayList<>();
        private ArrayList<Integer> estimated=  new ArrayList<>();

        public SecretCLass() {
        }

        public ArrayList<Integer> getEstimated() {
            return estimated;
        }

        public void setEstimated(ArrayList<Integer> estimated) {
            this.estimated = estimated;
        }

        public ArrayList<Integer> getAccepted() {
            return accepted;
        }

        public void setAccepted(ArrayList<Integer> accepted) {
            this.accepted = accepted;
        }

        public ArrayList<Integer> getRejected() {
            return rejected;
        }

        public void setRejected(ArrayList<Integer> rejected) {
            this.rejected = rejected;
        }
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
