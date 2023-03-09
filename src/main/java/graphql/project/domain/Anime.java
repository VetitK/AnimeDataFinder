package graphql.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Anime {
    private String id;
    private String title;
    private List<String> genres;
    private Integer year;
    private Integer score;
    private Integer popularity;
    private String image;
}
