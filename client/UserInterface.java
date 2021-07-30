import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;

public class UserInterface extends Application implements EventHandler<ActionEvent>{
    public int windowWidth;
    public int windowHeight;
    public TextArea displayCanvas;
    public Button startButton;
    public Thread clientThread;

    public String address;
    public int portNumber;
    public String channelID;
    private Client client;
    public boolean isConnected;

    public void parseArguments(String[] arguments){
        // Parsing Command Line Arguments
        System.out.println(arguments);
        for(int i=0;i<arguments.length;i++){
            if(arguments[i].equals("-a")){
                this.address= arguments[i+1];
            }else if(arguments[i].equals("-p")){
                this.portNumber = Integer.parseInt(arguments[i+1]);
            }else if(arguments[i].equals("-ch")){
                this.channelID=arguments[i+1];
            }
        }

        // Arguments After Parsing
        System.out.println(String.format("Adresss: %s",this.address));
        System.out.println(String.format("Port Number: %s",this.portNumber));
        System.out.println(String.format("Channel: %s",this.channelID));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Button createButton(String name){
        // Creat,ng button
        Button startButton = new Button();
        startButton.setText(name);
        startButton.setText(name);
        startButton.setOnAction(this);

        return startButton;
    }




    @Override
    public void start(Stage primaryStage) throws Exception{
        // Parsing command line arguments
        String[] tempArguments = getParameters().getRaw().stream().toArray(String[]::new);
        this.parseArguments(tempArguments);

        // Creating GUI
        windowWidth=650;
        windowHeight=500;

        Stage window = primaryStage;
        window.setTitle("BBM342 Video Stream");

        VBox layout = new VBox();
        layout.setSpacing(5);


        // Creating display TextArea for display frames
        displayCanvas = new TextArea();
        displayCanvas.setPrefWidth(windowWidth/(float)2);
        displayCanvas.setPrefHeight(windowHeight);
        displayCanvas.setEditable(false);
        displayCanvas.setCache(false);
        displayCanvas.setStyle("-fx-font-alignment: center;");


        this.startButton = createButton("Connect");
        Label mainText = new Label();
        mainText.setText(String.format("Welcome To the channel %s of BBm342 Video Service",this.channelID));


        // HBox for upper layout
        HBox upperLayout = new HBox();
        upperLayout.setSpacing(10);

        // Setting upper layout
        upperLayout.getChildren().addAll(mainText, startButton);
        upperLayout.setMargin(mainText ,new Insets(15,0,0,15));
        upperLayout.setMargin(startButton ,new Insets(15,0,0,260));


        // General layout
        layout.getChildren().addAll(upperLayout, displayCanvas);

        // Setting scene
        Scene scene = new Scene(layout, windowWidth,windowHeight);
        window.setScene(scene);
        window.setOnCloseRequest( (event) -> {
            onCloseHandler();
        });
        window.show();
    }

    public void onCloseHandler(){
        // Interrupting client thread on window close
        System.out.println("Closing Client...");
        if(this.clientThread != null){
            // Interrupting client thread
            this.clientThread.interrupt();
        }

    }

    @Override
    public void handle(ActionEvent event) {
        // Creating connection on button click
        if (event.getSource().equals(this.startButton)){
                System.out.println("Connecting...");
                this.isConnected=true;
                try {
                    createClientThread();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error on Creating Client Thread!");
                }
        }
    }

    public void createClientThread() throws IOException {
        // Creating Client thread
        Client client = new Client(new Socket(this.address, this.portNumber), this.displayCanvas, this.channelID);
        this.client = client;


        this.clientThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.receiveVideo();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        this.client.setThread(this.clientThread);
        this.clientThread.start();
    }
}
