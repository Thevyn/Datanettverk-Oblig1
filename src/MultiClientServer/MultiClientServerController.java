package MultiClientServer; /**
 * Multi-client socket server with JavaFX: MultiClientServerController - JavaFX controller
 * DATA/ITPE2410 Networking and Cloud Computing, Spring 2019
 * Raju Shrestha, OsloMet
 **/

import MultiClientServer.ServerTask;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MultiClientServerController implements Initializable
{
    @FXML
    public ListView<String> lstClients;
    @FXML
    public TextArea txtMessages;
    @FXML
    private TextArea txtLog;

    private ServerTask serverTask;

    public MultiClientServerController()
    {
        serverTask = new ServerTask(5555);
        serverTask.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        lstClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Listen to selection of an item in the list of clients, and accordingly update it's most recent message
        lstClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            public void changed(ObservableValue<? extends String> observableval, String oldval, String newval)
            {
                String client = lstClients.getSelectionModel().getSelectedItem();
                txtMessages.setText(serverTask.getClientMessageMap().get(client));
            }
        });

        // bind the list of clients stored as keys in the clientMessageMap to the itemsProperty of the lstClients
        // ListView
        ObservableMap<String, String> clientMessageMap = serverTask.getClientMessageMap();
        lstClients.itemsProperty().bind(Bindings.createObjectBinding(() ->
                    FXCollections.observableArrayList(clientMessageMap.keySet()), clientMessageMap));

        // bind the txtLog's text property with the message property of the serverTask object
        txtLog.textProperty().bind(serverTask.messageProperty());
    }

}
