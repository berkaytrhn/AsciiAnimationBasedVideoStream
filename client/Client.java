import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client{
    private Socket socket;
    private TextArea displayCanvas;
    private String channelNumber;
    private PrintWriter writer;
    private BufferedReader reader;
    private Thread thread;

    public Client(Socket socket, TextArea displayCanvas, String channelNumber) {
        this.socket = socket;
        this.displayCanvas = displayCanvas;
        this.channelNumber = channelNumber;
    }

    public void displayVideo(String receivedMessage) throws InterruptedException, IOException {
        // Method for show,ng video file frame by frame
        String[] messageArray = receivedMessage.split("1");
        for(int index=1;index<messageArray.length;index++){

            // Put Frame into TextArea
            String frame = messageArray[index];
            this.displayCanvas.setFont(Font.font("Courier New", 12));
            this.displayCanvas.setStyle("-fx-font-alignment: center;");
            this.displayCanvas.setText(frame);
            try{
                // Sleep Thread by 50ms, 20 frames per second
                Thread.sleep(50);
            }catch (InterruptedException exception){
                // If thread is sleeping on interrupt, this lines will run
                // Socket will  be closed and thread will  be interrupted after sleep.
                this.socket.close();
                this.thread.interrupt();
            }

        }
    }

    public void receiveVideo() throws IOException, InterruptedException {
        // Method for receiving video files from server
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        PrintWriter writer = new PrintWriter(this.socket.getOutputStream());
        this.reader = reader;
        this.writer = writer;
        //receive single message

        // Send channel number to server to let server know what to return
        writer.println(this.channelNumber);
        writer.flush();


        while(true) {
            //throws null pointer exception on server close
            //reading
            StringBuilder receivedMessage = new StringBuilder();
            String line;
            try {

                line = reader.readLine();
            }catch (SocketException exception){
                System.out.println("Socket closed, program terminating...");
                break;
            }
            int counter=0;
            while (!line.equals("break")) {
                // Reading buffer line by line until see break line
                if(counter++==0){
                    // first line of file, frame length but not used, frame length set to 50ms statically

                    // Parsing frame length part, in case to be used
                    //frameLength = Character.getNumericValue(line.charAt(0));
                }
                receivedMessage.append(line + "\n");
                line = reader.readLine();
            }
            // Sending channel number as message to server to let server know what to return
            writer.println(this.channelNumber);
            writer.flush();


            //  Display previous video before receive another one
            this.displayVideo(receivedMessage.toString());
        }
    }

    // Getter and Setters
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public TextArea getDisplayCanvas() {
        return displayCanvas;
    }

    public void setDisplayCanvas(TextArea displayCanvas) {
        this.displayCanvas = displayCanvas;
    }

    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
