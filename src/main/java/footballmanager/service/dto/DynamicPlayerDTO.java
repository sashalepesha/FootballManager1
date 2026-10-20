package footballmanager.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DynamicPlayerDTO(
    Long id,

    @NotBlank @Size(max = 100) String firstName,

    @NotBlank @Size(max = 100) String lastName,

    @NotNull @PastOrPresent LocalDate birthDate,

    @NotBlank @Size(max = 100) String nationality,

    @NotBlank @Size(max = 50) String position,

    @PositiveOrZero BigDecimal marketValue
) {}
