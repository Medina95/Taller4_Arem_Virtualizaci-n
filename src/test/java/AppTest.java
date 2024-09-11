import org.example.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AppTest {

    /**
     * Simula el repositorio MessageRepository utilizando Mockito.
     * Esto evita que se necesite una base de datos real para las pruebas.
     */
    @Mock
    private MessageRepository messageRepository;

    /**
     * Inyecta automáticamente el mock de MessageRepository en LogController.
     * LogController será probado sin necesidad de instanciar dependencias reales.
     */
    @InjectMocks
    private LogController logController;

    /**
     * Método que se ejecuta antes de cada prueba para inicializar los mocks.
     * Se utiliza para configurar los objetos simulados (mocks) que se usarán en las pruebas.
     */
    @BeforeEach
    public void setUp() {
        // Inicializa los mocks antes de ejecutar las pruebas
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Prueba el método saveMessage del LogController.
     * Verifica que se guarde un nuevo mensaje y que se devuelvan los 10 mensajes más recientes.
     */
    @Test
    public void testSaveMessageWithTop10Messages() {
        // Crea una lista de mensajes simulados con diferentes fechas.
        List<Message> mockMessages = Arrays.asList(
                createMockMessage("Mensaje 1", LocalDateTime.now().minusDays(1)),  // Mensaje de hace 1 día
                createMockMessage("Mensaje 2", LocalDateTime.now().minusDays(2)),  // Mensaje de hace 2 días
                createMockMessage("Mensaje 3", LocalDateTime.now().minusDays(3))   // Mensaje de hace 3 días
        );

        // Simula el comportamiento del repositorio para devolver los mensajes en orden descendente por fecha.
        when(messageRepository.findTop10ByOrderByDateDesc()).thenReturn(mockMessages);

        // Llama al método saveMessage con un nuevo mensaje de prueba.
        ResponseEntity<List<Message>> response = logController.saveMessage("Nuevo mensaje");

        // Verifica que el nuevo mensaje fue guardado en el repositorio.
        verify(messageRepository, times(1)).save(any(Message.class));

        // Verifica que el código de estado de la respuesta sea 200 (OK).
        assertEquals(200, response.getStatusCodeValue());

        // Obtiene los mensajes devueltos por el método y verifica su orden.
        List<Message> returnedMessages = response.getBody();
        assertEquals(3, returnedMessages.size());  // Verifica que se devuelven 3 mensajes.
        assertEquals("Mensaje 1", returnedMessages.get(0).getContent());  // El más reciente
        assertEquals("Mensaje 2", returnedMessages.get(1).getContent());  // El segundo más reciente
        assertEquals("Mensaje 3", returnedMessages.get(2).getContent());  // El más antiguo
    }

    /**
     * Prueba el método saveMessage cuando no hay mensajes previos en el repositorio.
     * Verifica que un mensaje nuevo se guarde correctamente.
     */
    @Test
    public void testSaveMessage() {
        // Crea una lista vacía para simular que no hay mensajes almacenados.
        List<Message> mockMessages = new ArrayList<>();

        // Simula el comportamiento del repositorio para devolver una lista vacía.
        when(messageRepository.findTop10ByOrderByDateDesc()).thenReturn(mockMessages);

        // Llama al método saveMessage con un nuevo mensaje de prueba.
        ResponseEntity<List<Message>> response = logController.saveMessage("Mensaje de prueba");

        // Verifica que el nuevo mensaje fue guardado en el repositorio.
        verify(messageRepository, times(1)).save(any(Message.class));

        // Verifica que el código de estado de la respuesta sea 200 (OK).
        assertEquals(200, response.getStatusCodeValue());

        // Verifica que la lista de mensajes en la respuesta sea la esperada.
        assertEquals(mockMessages, response.getBody());
    }

    /**
     * Método auxiliar para crear un mensaje simulado con contenido y fecha.
     * @param content El contenido del mensaje.
     * @param date La fecha del mensaje.
     * @return Un objeto Message con los valores proporcionados.
     */
    private Message createMockMessage(String content, LocalDateTime date) {
        // Crea una instancia de Message y configura su contenido y fecha.
        Message message = new Message();
        message.setContent(content);       // Establece el contenido del mensaje.
        message.setDate(date.toString());  // Establece la fecha del mensaje como cadena.
        return message;                    // Devuelve el mensaje simulado.
    }
}
