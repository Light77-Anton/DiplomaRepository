package main.model;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ModerationStatusConverter implements AttributeConverter<ModerationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ModerationStatus status) {
        if (status == ModerationStatus.ACCEPTED) {
            return "ACCEPTED";
        }
        else if (status == ModerationStatus.DECLINED) {
            return "DECLINED";
        } else {
            return "NEW";
        }
    }

    @Override
    public ModerationStatus convertToEntityAttribute(String s) {
        if (s.equals("ACCEPTED")) {
            return ModerationStatus.ACCEPTED;
        }
        else if (s.equals("DECLINED")) {
            return ModerationStatus.DECLINED;
        } else {
            return ModerationStatus.NEW;
        }
    }
}
