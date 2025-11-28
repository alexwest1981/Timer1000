package org.example.timer1000;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {
    private static final String MEMBER_FILE = "members.json";
    private static final String LOG_FILE = "timelogs.json";
    private static final ObjectMapper mapper = new ObjectMapper().
            enable(SerializationFeature.INDENT_OUTPUT);

    public static List<Member> loadMembers() {
        try {
            File file = new File(MEMBER_FILE);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Member.class));
        } catch (IOException e) {
            System.err.println("Fel vid laddning av medlemmar från JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveMember(Member member) {
        List<Member> members = loadMembers();

        if (member.getId() == 0) {
            int maxId = members.stream()
                    .mapToInt(Member::getId)
                    .max().orElse(0);
            member.setId(maxId + 1);
        }

        members.add(member);

        try {
            mapper.writeValue(new File(MEMBER_FILE), members);
        } catch (IOException e) {
            System.err.println("Fel vid sparande av medlem till JSON: " + e.getMessage());
        }
    }

    public static List<TimeLogEntry> loadTimeLogs() {
        try {
            File file = new File(LOG_FILE);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, TimeLogEntry.class));
        } catch (IOException e) {
            System.err.println("Error while loading " + LOG_FILE + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveTimeLog(int memberId, long durationSeconds) {
        List<TimeLogEntry> logs = loadTimeLogs();
        logs.add(new TimeLogEntry(memberId, durationSeconds));

        try {
            mapper.writeValue(new File(LOG_FILE), logs);
        } catch (IOException e) {
            System.err.println("Error while saving " + LOG_FILE + ": " + e.getMessage());
        }
    }
}
