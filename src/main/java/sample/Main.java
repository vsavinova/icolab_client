package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.beans.EventHandler;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private String login = "login";
    private  String password = "123456";

    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox root = new VBox();//FXMLLoader.load(getClass().getResource("sample.fxml"));
        VBox header = new VBox();
        Label account = new Label("account");
        Label address = new Label("address");
        Label balance = new Label("balance");
        HBox hBox = new HBox();

        VBox history = getHistoryPane(3);
        VBox invoicesBox = getInvoicePane();

        hBox.getChildren().addAll(invoicesBox, history);
        invoicesBox.setMaxWidth(Double.MAX_VALUE);
        history.setMaxWidth(Double.MAX_VALUE);

        hBox.setHgrow(history, Priority.ALWAYS);
        hBox.setHgrow(invoicesBox, Priority.ALWAYS);

        header.getChildren().addAll(account, address, balance);
        header.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderStroke.THIN)));

        root.getChildren().addAll(header, hBox);
        primaryStage.setTitle("ICO");
//        primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }


    private VBox getHistoryPane(int size){
        VBox historyBox = new VBox();
        VBox vFramesBox = new VBox();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);

        VBox lblBox = new VBox();
        lblBox.setBackground(getBackground(Color.LIGHTGRAY));

        Label historyLbl = new Label("History");
        historyLbl.setAlignment(Pos.BASELINE_CENTER);
        historyLbl.setPrefSize(150, 30);
        lblBox.getChildren().add(historyLbl);

        historyBox.setBorder(getBorder());
        historyBox.setFillWidth(true);

        // Adding elements
        historyBox.getChildren().add(lblBox);
        for (int i=0; i < size; i++)
            vFramesBox.getChildren().add(drawHistoryInvoice("Old invoice"));

        scrollPane.setContent(vFramesBox);
        historyBox.getChildren().add(vFramesBox);
        return historyBox;
    }

    private VBox getInvoicePane(){
        VBox invoicesBox = new VBox();
        VBox lblBox = new VBox();
        lblBox.setBackground(getBackground(Color.LIGHTGRAY));

        Label invoicesLbl = new Label("Invoices");
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

    private VBox drawHistoryInvoice(String title){
        VBox root = new VBox();
        VBox localRoot = new VBox();
        Label titleLbl = new Label(title);
//        Label description = new Label("Some description ...");
        titleLbl.setPrefSize(150, 30);
        titleLbl.setTextAlignment(TextAlignment.CENTER);
        titleLbl.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        List<Button> buttons = new ArrayList<Button>();
        Button bad = new Button("bad");
        Button well = new Button("well");
        Button good = new Button("good");
        buttons.add(bad);
        buttons.add(well);
        buttons.add(good);
        buttons.forEach((b)->b.setOnAction(this::sendAssessment));
        hBox.getChildren().addAll(buttons);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        localRoot.getChildren().addAll(titleLbl, hBox/*, description*/);
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
        root.getChildren().add(localRoot);
//        localRoot.setSpacing(30);
//        localRoot.setPadding(getInsets(15, 15, 15, 15));
        root.setPadding(getInsets(30,30,30,30));
//        root.setBackground(getBackground());

        return root;
    }

    private void sendAccept(ActionEvent event) {
        // TODO: send to the contract
    }

    private void sendReject(ActionEvent event) {
        // TODO: send to the contract
    }

    private void sendAssessment(ActionEvent event){
        String value = ((Button) event.getSource()).getText();
        switch (value){
            case "bad" :
                // TODO send to the contract
                System.out.println("Bad was pressed");
                break;
            case "well":
                // TODO send to the contract
                break;
            case "good":
                // TODO send to the contract
                break;
        }
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
