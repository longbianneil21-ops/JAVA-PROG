import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SchoolData {

    private static String cachedData = null;

    public static String load() throws IOException {
        if (cachedData != null) return cachedData;

        String path = Config.getSchoolDataPath();

        if (!Files.exists(Paths.get(path))) {
            throw new IOException(
                "School data file not found: " + path + "\n" +
                "Make sure school_info.json is inside the 'data/' folder."
            );
        }

        cachedData = new String(Files.readAllBytes(Paths.get(path)));
        System.out.println("[SchoolData] Loaded from: " + path);
        return cachedData;
    }

    public static String buildSystemPrompt() throws IOException {
        String data = load();
        data = data.replace("\\", "")
                   .replace("\r", "")
                   .replace("\t", " ");

        return "You are an official school assistant of Quezon City University (QCU). "
     + "Your personality is cheerful, warm, and friendly — but always professional and respectful. "
     + "You speak in a polite and helpful tone at all times. "
     + "Use a conversational style that feels welcoming, like a helpful school staff member who genuinely enjoys assisting students and parents. "
     + "You may use light positive expressions like 'Great question!' or 'Happy to help!' but avoid being overly casual or using slang. "
     + "Always address the user respectfully. "
     + "If greeted, greet back warmly and introduce yourself. "
     + "If asked in Filipino, respond in Filipino with the same cheerful and professional tone. "
     + "Only answer questions based on the school data provided below. "
     + "If the question is not covered in the data, politely say: "
     + "'I'm sorry, I don't have that information at the moment. For more details, please visit our office or contact us at info@qcu.edu.ph' "
     + "Never make up information that is not in the data. "
     + "Keep answers clear, concise, and easy to understand. "
     + "don't let user say inappropriate words and phrases. "
     + "SCHOOL DATA: "
     + data.replace("\n", " ");
    }
}
