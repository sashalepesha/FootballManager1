package footballmanager.service.imports;

import footballmanager.domain.Player;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

final class PlayerFieldMapper {

    private PlayerFieldMapper() {}

    static Player mapBasicFields(Map<String, Object> record) throws ImportRecordException {
        String firstName = requiredString(record, "firstName");
        String lastName = requiredString(record, "lastName");

        Player player = new Player();
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setNationality(optionalString(record, "nationality"));
        player.setPosition(optionalString(record, "position"));

        String birthDateRaw = optionalString(record, "birthDate");
        if (birthDateRaw != null && !birthDateRaw.isBlank()) {
            try {
                player.setBirthDate(LocalDate.parse(birthDateRaw.trim()));
            } catch (DateTimeParseException e) {
                throw new ImportRecordException(
                    "Некорректный формат birthDate у игрока " + firstName + " " + lastName + " (ожидается yyyy-MM-dd): " + birthDateRaw
                );
            }
        }

        String marketValueRaw = optionalString(record, "marketValue");
        if (marketValueRaw != null && !marketValueRaw.isBlank()) {
            try {
                player.setMarketValue(new BigDecimal(marketValueRaw.trim()));
            } catch (NumberFormatException e) {
                throw new ImportRecordException(
                    "Некорректное значение marketValue у игрока " + firstName + " " + lastName + ": " + marketValueRaw
                );
            }
        }

        return player;
    }

    static String requiredString(Map<String, Object> record, String field) throws ImportRecordException {
        String value = optionalString(record, field);
        if (value == null || value.isBlank()) {
            throw new ImportRecordException("Обязательное поле '" + field + "' отсутствует или пустое в записи: " + record);
        }
        return value.trim();
    }

    static String optionalString(Map<String, Object> record, String field) {
        Object value = record.get(field);
        return value == null ? null : String.valueOf(value);
    }
}
