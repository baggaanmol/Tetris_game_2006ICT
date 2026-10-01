package tetris.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Shared JavaFX layout helpers for the application's menu and information
 * screens. 
 */
public final class ScreenLayout {
    private ScreenLayout() {
    }

    public static BorderPane createPage(String title, String subtitle) {
        BorderPane page = new BorderPane();
        page.getStyleClass().add("app-page");

        VBox header = new VBox(4);
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER);

        Label heading = new Label(title);
        heading.getStyleClass().add("page-title");
        Label supportingText = new Label(subtitle);
        supportingText.getStyleClass().add("subtitle");
        header.getChildren().addAll(heading, supportingText);

        page.setTop(header);
        BorderPane.setMargin(header, new Insets(26, 24, 12, 24));
        return page;
    }
}