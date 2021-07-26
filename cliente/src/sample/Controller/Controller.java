package sample.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import sample.Model.ThreadClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {

    private Socket socket;
    private DataOutputStream bufferDeSalida = null;

    @FXML
    private Button btniniciarChat;
    @FXML
    private Button btnSalir;

@FXML
private ComboBox<String> cmbListaUSuarios;

    @FXML
    private Label lblEstadoConexion;

    @FXML
    private Label lblmiIDUsuario;

    @FXML
    private Label lblNumUsuariosConectados;

    @FXML
    private Button btnConectar;

    @FXML
    private TextField ipServer;

    @FXML
    private TextField portServer;

    @FXML
    private TextArea log;

    @FXML
    private ListView<String> lvMensajes;

    @FXML
    private TextField txtEnviar;

    @FXML
    private Button btnEnviar;

    @FXML
    private Circle circle;

public void initialize (){
    try {
        socket = new Socket("localhost", Integer.valueOf("3001"));
        lblEstadoConexion.setText("Conectado");
        bufferDeSalida = new DataOutputStream(socket.getOutputStream());
        bufferDeSalida.flush();
        ThreadClient cliente = new ThreadClient(socket,log,lvMensajes,cmbListaUSuarios,lblmiIDUsuario,lblNumUsuariosConectados);
        cliente.addObserver(this);
        new Thread(cliente).start();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    @FXML
    void btnSalirOnMouseClicked(MouseEvent event) {
        Platform.exit();
        System.exit(1);
    }

    @FXML
    void btnEnviarOnMouseClicked(MouseEvent event) {

        try {
            System.out.println(cmbListaUSuarios.getSelectionModel().getSelectedItem());
            if(cmbListaUSuarios.getSelectionModel().getSelectedItem().equals("Todos")){
                for(int i =0 ; i<Integer.valueOf(lblNumUsuariosConectados.getText()); i++){
                    bufferDeSalida.writeUTF("3:"+lblmiIDUsuario.getText()+":"+"usuario"+i+":"+txtEnviar.getText());
                    bufferDeSalida.writeUTF("1:1");
                    bufferDeSalida.flush();

                }
                lvMensajes.getItems().add("Yo para "+ "Todos"+ ": "+ txtEnviar.getText());
            }
            else {
                bufferDeSalida.writeUTF("3:" + lblmiIDUsuario.getText() + ":" + cmbListaUSuarios.getSelectionModel().getSelectedItem() + ":" + txtEnviar.getText());
                bufferDeSalida.writeUTF("1:1");
                bufferDeSalida.flush();
                lvMensajes.getItems().add("Yo para " + cmbListaUSuarios.getSelectionModel().getSelectedItem() + ": " + txtEnviar.getText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtEnviar.clear();
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
