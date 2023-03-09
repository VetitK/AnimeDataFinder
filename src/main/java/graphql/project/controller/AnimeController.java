package graphql.project.controller;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import graphql.project.domain.Anime;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;

import graphql.project.Query;
public class AnimeController implements Initializable{
    public TableView<Anime> animeTableView;
    public TextField titleTextField;
    public TextField genresTextField;
    public TextField yearTextField;
    public TextField numQueries;
    public ImageView imageView;

    public Label statusLabel;
    public Label currentPage;
    public Label totalPages;

    private Anime selectedAnime;
    public Button leftPagnition = new Button("leftPagnition");;
    public Button rightPagnition = new Button("rightPagnition");
    public HBox chartsPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableView();
        loadData(1, 50);
    }

    private void prepareTableView() {
        TableColumn<Anime, String> idColumn = new TableColumn<Anime, String>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<Anime, String>("id"));
        TableColumn<Anime, String> titleColumn = new TableColumn<Anime, String>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<Anime, String>("title"));
        TableColumn<Anime, List<String>> genresColumn = new TableColumn<Anime, List<String>>("Genres");
        genresColumn.setCellValueFactory(new PropertyValueFactory<Anime, List<String>>("genres"));
        TableColumn<Anime, String> yearColumn = new TableColumn<Anime, String>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<Anime, String>("year"));
        TableColumn<Anime, String> scoreColumn = new TableColumn<Anime, String>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<Anime, String>("score"));
        TableColumn<Anime, String> popularityColumn = new TableColumn<Anime, String>("Popularity");
        popularityColumn.setCellValueFactory(new PropertyValueFactory<Anime, String>("popularity"));

        animeTableView.getColumns().add(idColumn);
        animeTableView.getColumns().add(titleColumn);
        animeTableView.getColumns().add(genresColumn);
        animeTableView.getColumns().add(yearColumn);
        animeTableView.getColumns().add(scoreColumn);
        animeTableView.getColumns().add(popularityColumn);
    }

    private void loadData(int page, int num) {
        JsonObject animesObj = Query.getPlaceHolderAnime(num, page);
        setNumPage(animesObj);
        List<Anime> animeList = new ArrayList<Anime>();
        animesObj.get("data").getAsJsonObject().get("Page").getAsJsonObject().get("media").getAsJsonArray().forEach(anime -> {
            Anime animeObj = new Anime();
            
            animeObj.setId(anime.getAsJsonObject().get("id").getAsString());
            animeObj.setTitle(anime.getAsJsonObject().get("title").getAsJsonObject().get("romaji").getAsString());
            // Get as array
            List<String> stringList = new ArrayList<String>();
            JsonArray jsonArray = anime.getAsJsonObject().get("genres").getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                String string = jsonArray.get(i).getAsString();
                stringList.add(string);
            }
            animeObj.setGenres(stringList);
            // handle null
            if (!anime.getAsJsonObject().get("startDate").isJsonNull()) {
                animeObj.setYear(anime.getAsJsonObject().get("startDate").getAsJsonObject().get("year").getAsInt());
            } else {
                animeObj.setYear(0);
            }
            animeObj.setScore(anime.getAsJsonObject().get("meanScore").getAsInt());
            animeObj.setPopularity(anime.getAsJsonObject().get("popularity").getAsInt());
            animeObj.setImage(anime.getAsJsonObject().get("coverImage").getAsJsonObject().get("extraLarge").getAsString());
            animeList.add(animeObj);
        });

        animeTableView.getItems().clear();
        animeTableView.getItems().addAll(animeList);
    }

    public void clearForm(ActionEvent event) {
        titleTextField.setText("");
        genresTextField.setText("");
        yearTextField.setText("");
    }

    public void resetTable(ActionEvent event) {
        animeTableView.getItems().clear();
        loadData(1, 50);
    }

    public void searchAnime(ActionEvent event) {
        
        String title = titleTextField.getText();
        String genres = genresTextField.getText();
        String year = yearTextField.getText();
        Integer currentPage;
        Integer num = Integer.parseInt(numQueries.getText());
        if ( event.getSource().toString().contains("searchBtn") ){
            currentPage = 1;
        } else {
            currentPage = Integer.parseInt(this.currentPage.getText());
        }
        // Only title and genre are allowed at the same time, year must be search alone
        if (title.isEmpty() && genres.isEmpty() && year.isEmpty()) {
            loadData(1, num);
            return;
        }

        JsonObject animesObj = Query.getAnimeByName(title, genres, year, currentPage, num);
        setNumPage(animesObj);
        List<Anime> animeList = new ArrayList<Anime>();
        animesObj.get("data").getAsJsonObject().get("Page").getAsJsonObject().get("media").getAsJsonArray().forEach(anime -> {
            Anime animeObj = new Anime();
            animeObj.setId(anime.getAsJsonObject().get("id").getAsString());
            animeObj.setTitle(anime.getAsJsonObject().get("title").getAsJsonObject().get("romaji").getAsString());
            // Get as array
            List<String> stringList = new ArrayList<String>();
            JsonArray jsonArray = anime.getAsJsonObject().get("genres").getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                String string = jsonArray.get(i).getAsString();
                stringList.add(string);
            }
            animeObj.setGenres(stringList);
            // animeObj.setYear(anime.getAsJsonObject().get("seasonYear").getAsInt()); // TODO: Fix this
            // Handle null
            if (!anime.getAsJsonObject().get("startDate").isJsonNull()) {
                animeObj.setYear(anime.getAsJsonObject().get("startDate").getAsJsonObject().get("year").getAsInt());
            } else {
                animeObj.setYear(0);
            }
            if (!anime.getAsJsonObject().get("meanScore").isJsonNull()) {
                animeObj.setScore(anime.getAsJsonObject().get("meanScore").getAsInt());
            } else {
                animeObj.setScore(0);
            }
            if (!anime.getAsJsonObject().get("popularity").isJsonNull()) {
                animeObj.setPopularity(anime.getAsJsonObject().get("popularity").getAsInt());
            } else {
                animeObj.setPopularity(0);
            }
            animeObj.setImage(anime.getAsJsonObject().get("coverImage").getAsJsonObject().get("extraLarge").getAsString());
            animeList.add(animeObj);
        });

        animeTableView.getItems().clear();
        animeTableView.getItems().addAll(animeList);

    }

    public void setLabel(Label label, String text) {
        label.setText(text);
    }

    public void setNumPage(JsonObject animesObj) {
        JsonObject pageInfo = animesObj.get("data").getAsJsonObject().get("Page").getAsJsonObject().get("pageInfo").getAsJsonObject();
        System.out.println(pageInfo);
        int numPage = pageInfo.get("lastPage").getAsInt();
        int currentPage = pageInfo.get("currentPage").getAsInt();
        int numItems = pageInfo.get("total").getAsInt();
        if (!pageInfo.get("hasNextPage").getAsBoolean()) {
            // Set Button to disabled
            rightPagnition.setDisable(true);
        } else {
            rightPagnition.setDisable(false);
        }
        if (currentPage == 1) {
            // Set Button to disabled
            leftPagnition.setDisable(true);
        } else {
            leftPagnition.setDisable(false);
        }
        setLabel(this.currentPage, String.valueOf(currentPage));
        setLabel(this.totalPages, String.valueOf(numPage));
        setLabel(this.statusLabel, String.valueOf(numItems) + " items found");
    }

    public void previousPage(ActionEvent event) {
        currentPage.setText(String.valueOf(Integer.parseInt(currentPage.getText()) - 1));
        int num = Integer.parseInt(numQueries.getText());
        if (titleTextField.getText().isEmpty() && genresTextField.getText().isEmpty() && yearTextField.getText().isEmpty())
            loadData(Integer.valueOf(currentPage.getText()), num);
        else
            searchAnime(event);
    }

    public void nextPage(ActionEvent event) {
        currentPage.setText(String.valueOf(Integer.parseInt(currentPage.getText()) + 1));
        int num = Integer.parseInt(numQueries.getText());
        if (titleTextField.getText().isEmpty() && genresTextField.getText().isEmpty() && yearTextField.getText().isEmpty())
            loadData(Integer.valueOf(currentPage.getText()), num);
        else
            searchAnime(event);
    }

    public void animeTableMouseClicked(MouseEvent event) {
        selectedAnime = animeTableView.getSelectionModel().getSelectedItem();
        if (selectedAnime == null) {
            return;
        }
        String imString = selectedAnime.getImage();
        Image image = new Image(imString);
        imageView.setImage(image);
    }
}