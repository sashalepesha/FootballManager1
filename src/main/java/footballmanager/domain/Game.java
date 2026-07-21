package footballmanager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "game")
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Match date is required")
    @Column(name = "match_date", nullable = false)
    private Instant matchDate;

    @NotBlank(message = "Stadium is required")
    @Size(max = 100, message = "Stadium must contain at most 100 characters")
    @Column(name = "stadium")
    private String stadium;

    @NotNull(message = "Home score is required")
    @PositiveOrZero(message = "Home score cannot be negative")
    @Column(name = "home_score")
    private Integer homeScore;

    @NotNull(message = "Away score is required")
    @PositiveOrZero(message = "Away score cannot be negative")
    @Column(name = "away_score")
    private Integer awayScore;

    @NotNull(message = "Home team is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @NotNull(message = "Away team is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Instant matchDate) {
        this.matchDate = matchDate;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    /**
     * Проверка, что команды разные.
     */
    @AssertTrue(message = "Home team and away team must be different")
    public boolean isTeamsDifferent() {
        if (homeTeam == null || awayTeam == null) {
            return true;
        }

        if (homeTeam.getId() == null || awayTeam.getId() == null) {
            return true;
        }

        return !homeTeam.getId().equals(awayTeam.getId());
    }
}
