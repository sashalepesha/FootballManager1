package footballmanager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transfer")
public class Transfer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Player is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @NotNull(message = "Source team is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "from_team_id", nullable = false)
    private Team fromTeam;

    @NotNull(message = "Destination team is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "to_team_id", nullable = false)
    private Team toTeam;

    @NotNull(message = "Transfer fee USD is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Transfer fee USD cannot be negative")
    @Column(name = "transfer_fee_usd", precision = 21, scale = 2, nullable = false)
    private BigDecimal transferFeeUsd;

    @NotNull(message = "Transfer fee BYN is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Transfer fee BYN cannot be negative")
    @Column(name = "transfer_fee_byn", precision = 21, scale = 2, nullable = false)
    private BigDecimal transferFeeByn;

    @NotNull(message = "Transfer date is required")
    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getFromTeam() {
        return fromTeam;
    }

    public void setFromTeam(Team fromTeam) {
        this.fromTeam = fromTeam;
    }

    public Team getToTeam() {
        return toTeam;
    }

    public void setToTeam(Team toTeam) {
        this.toTeam = toTeam;
    }

    public BigDecimal getTransferFeeUsd() {
        return transferFeeUsd;
    }

    public void setTransferFeeUsd(BigDecimal transferFeeUsd) {
        this.transferFeeUsd = transferFeeUsd;
    }

    public BigDecimal getTransferFeeByn() {
        return transferFeeByn;
    }

    public void setTransferFeeByn(BigDecimal transferFeeByn) {
        this.transferFeeByn = transferFeeByn;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }
}
