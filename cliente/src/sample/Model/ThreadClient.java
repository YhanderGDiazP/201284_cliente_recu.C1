
package sample.Model;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadClient extends Observable implements Runnable {
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private TextArea log;
    private ListView<String> lvMensajes;
    private ComboBox<String> cmbListaUSuarios;
    private Label lblmiIDUsuario;
    private Label lblNumUsuariosConectados;
    private int contUsuarios;

    public ThreadClient(Socket socket, TextArea log, ListView<String> lvMensajes, ComboBox<String> cmbListaUSuarios, Label lblmiIDUsuario, Label lblNumUsuariosConectados) {
        this.socket = socket;
        this.log = log;
this.lvMensajes = lvMensajes;
this.cmbListaUSuarios = cmbListaUSuarios;
this.lblmiIDUsuario = lblmiIDUsuario;
this.lblNumUsuariosConectados = lblNumUsuariosConectados;

    }

    public void run() {

        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            String st = "";

            do {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(1000L)+100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    st = bufferDeEntrada.readUTF();



                    String[] datagrama;
                    datagrama = st.split(":");

                    if (datagrama[0].equals("1")) {
                        System.out.println("El usuario "+ datagrama[3] + " Se a conectado");
                        System.out.println("Mi usuario es"+ datagrama[2]);
                        Platform.runLater(() ->  { lvMensajes.getItems().add("***El: "+" ["+ datagrama[3]+"] " + " se a conectado***");  });
                        String[] listaClientesconectados;
                        listaClientesconectados = st.split(":",4);
                        final String stt = listaClientesconectados[3];
                        listaUsuarios(stt);
                        Platform.runLater(() ->  { lblmiIDUsuario.setText(datagrama[2]); });
                        Platform.runLater(() ->  { cmbListaUSuarios.getItems().clear();  });
                        Platform.runLater(() ->  { cmbListaUSuarios.getItems().addAll("Todos");  });
                        Platform.runLater(() ->  { cmbListaUSuarios.getItems().addAll(listaUsuarios(stt));  });
                        Platform.runLater(() ->  { lblNumUsuariosConectados.setText(String.valueOf(contUsuarios)); });
                        System.out.println(contUsuarios);
                    }
                   else {
                        System.out.println(st);
                        final String sms = st;
                        Platform.runLater(() ->  { lvMensajes.getItems().add(sms);  });
                    }
                    String[] array = st.split(":");
                    this.setChanged();
                    this.notifyObservers(st);
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }while (!st.equals("FIN"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String[] listaUsuarios(String st){
        contUsuarios=0;
        String[] datagrama;
        datagrama = st.split(":");
        String[] usuarios = new String[datagrama.length];
        for(int i=0; i<datagrama.length; i++){
usuarios[i] = datagrama[i];
            contUsuarios= i;
        }
        return usuarios;
    }


}
