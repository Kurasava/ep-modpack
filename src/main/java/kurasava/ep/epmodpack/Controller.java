package kurasava.ep.epmodpack;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Controller {
   public CheckBox checkBoxAddServers;
   public Button buttonSelectDirectory;
   public TextField textFieldDirectory;
   public Button buttonOptionalMods;
   public MenuButton menuButtonVersions;
   @FXML
   private Pane root;
   public Pane header;
   public Button buttonInstall;
   double xOffSet;
   double yOffSet;

   @FXML
   private void getLocationWindow(MouseEvent e) {
      this.xOffSet = e.getSceneX();
      this.yOffSet = e.getSceneY();
   }

   @FXML
   private void setLocationWindow(MouseEvent e) {
      Stage stage = (Stage)this.header.getScene().getWindow();
      stage.setX(e.getScreenX() - this.xOffSet);
      stage.setY(e.getScreenY() - this.yOffSet);
   }
}
