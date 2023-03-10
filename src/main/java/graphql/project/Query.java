package graphql.project;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;



public class Query {
    private static String getQueryString(String filePath) {
        try {
            FileReader reader = new FileReader(filePath);
            int character;
            StringBuilder builder = new StringBuilder();
            while ((character = reader.read()) != -1) {
            builder.append((char) character);}
            reader.close();
            return builder.toString();
        }
        catch (IOException error) {
            System.out.println(error.getMessage());
            return "";
        }
    }
    public static JsonObject getAnimeByID(String animeID) {
        String queryString = "query($id: Int) { Media(id: $id, type: ANIME) { id title { romaji english native } } }";
        String variables = String.format("{ \"id\": %s }", animeID);
        try {
            HttpResponse httpResponse = callGraphQLService("https://graphql.anilist.co", queryString, variables);
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
            return jsonObject;
        } catch (IOException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        } catch (URISyntaxException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        }
    }

    public static HttpResponse callGraphQLService(String url, String query, String variables) 
        throws URISyntaxException, IOException {
        System.out.println(variables);
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        URI uri = new URIBuilder(request.getURI())
            .addParameter("query", query)
            .addParameter("variables", variables)
            .build();
        request.setURI(uri);
        return client.execute(request);
    }

    public static JsonArray getAllGenre(String[] args) {
        String queryString = " { GenreCollection }";
        try {
            HttpResponse httpResponse = callGraphQLService("https://graphql.anilist.co", queryString, null);
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
            JsonArray allGenre = jsonObject.get("data").getAsJsonObject().get("GenreCollection").getAsJsonArray();
            return allGenre;
        } catch (IOException error) {
            System.out.println(error.getMessage());
        } catch (URISyntaxException error) {
            System.out.println(error.getMessage());
        }
        return null;
    }

    public static JsonObject getAnimesByGenre(String genre, int page, int perPage) {
        // String queryString;
        // try {
        //     FileReader reader = new FileReader("src/main/java/graphql/project/graphql/getByGenre.graphql");
        //     int character;
        //     StringBuilder builder = new StringBuilder();
        //     while ((character = reader.read()) != -1) {
        //     builder.append((char) character);}
        //     reader.close();
        //     queryString = builder.toString();
        // }
        // catch (IOException error) {
        //     System.out.println(error.getMessage());
        //     queryString = "";
        // }
        String queryString = getQueryString("src/main/java/graphql/project/graphql/getByGenre.graphql");
        String variables = String.format("{ \"genre\": \"%s\", \"page\": \"%d\", \"perPage\": \"%d\"  }", genre, page, perPage);
        try {
            HttpResponse httpResponse = callGraphQLService("https://graphql.anilist.co", queryString, variables);
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
            return jsonObject;
        } catch (IOException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        } catch (URISyntaxException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        }
    }

    public static JsonObject getAnimeByName(String name, String genre, String year, int page, int perPage) {

        String formatStartYear = year + "0101";
        String formatEndYear = year + "1231";
        Integer yearInt = Integer.parseInt(formatStartYear);
        Integer yearEndInt = Integer.parseInt(formatEndYear);
        // String formatYearEnd = year + "1231";
        String queryString = getQueryString("src/main/java/graphql/project/graphql/getByName.graphql");

        Map<String, Object> variables = new HashMap<>();
        if (!name.isEmpty()) {
            variables.put("name", name);
        }
        if (!genre.isEmpty()) {
            variables.put("genre_in", genre);
        }
        if (!year.isEmpty()) {
            variables.put("startDate", yearInt);
            variables.put("endDate", yearEndInt);
        }
        variables.put("page", page);
        variables.put("perPage", perPage);
        String variablesString = new Gson().toJson(variables);
        // String variables = String.format("{ \"name\": \"%s\", \"genre_in\": [\"%s\"], \"startDate \": \"%d\", \"page\": \"%d\", \"perPage\": \"%d\"  }",
        //                                 "Attack on titan", "Action", 20130000, page, perPage);
        try {
            HttpResponse httpResponse = callGraphQLService("https://graphql.anilist.co", queryString, variablesString);
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                return jsonObject;
        } catch (IOException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        } catch (URISyntaxException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        }
    }

    public static JsonObject getPlaceHolderAnime(int amount, int page) {
        String queryString = getQueryString("src/main/java/graphql/project/graphql/getPlaceHolderAnime.graphql");
        String variables = String.format("{ \"perPage\": \"%d\", \"page\": \"%d\" }", amount, page);
        try {
            HttpResponse httpResponse = callGraphQLService("https://graphql.anilist.co", queryString, variables);
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
            return jsonObject;
        } catch (IOException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        } catch (URISyntaxException error) {
            System.out.println(error.getMessage());
            return new JsonObject();
        }
    }

    public static void main(String[] args) {
        JsonObject animes = getAnimeByName("Your Name", "", "", 1, 10);
        System.out.println(animes);
    }
}
