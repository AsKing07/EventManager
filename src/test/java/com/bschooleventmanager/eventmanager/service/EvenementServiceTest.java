package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
//import static org.mockito.Mockito.*;

class EvenementServiceTest {

    private EvenementDAO mockEvenementDAO;

    /*@BeforeEach
    void setUp() throws Exception {
        mockEvenementDAO = mock(EvenementDAO.class);

        // Injecter le mock dans le champ privé static final via réflexion
        Field field = EvenementService.class.getDeclaredField("evenementDAO");
        field.setAccessible(true);

        // Retirer le flag final pour pouvoir remplacer la valeur (nécessaire en test)
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, mockEvenementDAO);
    }

    @Test
    void suppEvent_shouldCallDaoWithGivenId() {
        int id = 123;
        // Appel de la méthode testée
        EvenementService.suppEvent(id);

        // Vérifier que la DAO a bien été appelée avec l'id
        verify(mockEvenementDAO, times(1)).suppEvent(id);
    }

    @Test
    void suppEvent_whenDaoThrowsRuntimeException_shouldPropagate() {
        int id = 7;
        doThrow(new RuntimeException("DB error")).when(mockEvenementDAO).suppEvent(id);

        // Vérifie que l'exception lancée par la DAO est propagée
        assertThrows(RuntimeException.class, () -> EvenementService.suppEvent(id));

        verify(mockEvenementDAO, times(1)).suppEvent(id);
    }*/
}
