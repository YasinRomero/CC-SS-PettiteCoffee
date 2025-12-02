package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase.PurchaseRequestDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase.PurhcaseHistoryDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.PurchaseService;

@ExtendWith(MockitoExtension.class)
public class PurchaseControllerTest {

    /**
     * Pruebas unitarias para PurchaseController: recuperación de historial y creación de compras.
     */

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController controller;

    private PurhcaseHistoryDTO hist;

    @BeforeEach
    void setUp() {
        hist = mock(PurhcaseHistoryDTO.class);
    }

    @Test
    /**
     * Verifica que getPurchases devuelve OK con el historial de compras cuando el servicio tiene éxito.
     */
    void testGetPurchases_Success() {
        CustomUserDetails user = mock(CustomUserDetails.class);
        when(purchaseService.getHistory(user)).thenReturn(List.of(hist));

        ResponseEntity<?> resp = controller.getPurchases(user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(purchaseService).getHistory(user);
    }

    @Test
    /**
     * Verifica que getPurchases devuelve INTERNAL_SERVER_ERROR cuando el servicio lanza una excepción.
     */
    void testGetPurchases_Exception() {
        CustomUserDetails user = mock(CustomUserDetails.class);
        when(purchaseService.getHistory(user)).thenThrow(new RuntimeException("err"));

        ResponseEntity<?> resp = controller.getPurchases(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        verify(purchaseService).getHistory(user);
    }

    @Test
    /**
     * Verifica que newPurchase devuelve OK cuando guardar tiene éxito y INTERNAL_SERVER_ERROR cuando falla.
     */
    void testNewPurchase_Success_and_Exception() {
        CustomUserDetails user = mock(CustomUserDetails.class);
        PurchaseRequestDTO request = new PurchaseRequestDTO();

        doNothing().when(purchaseService).savePurchase(user, request);
        ResponseEntity<?> resp = controller.newPurchase(request, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(purchaseService).savePurchase(user, request);

        doThrow(new RuntimeException("boom")).when(purchaseService).savePurchase(user, request);
        ResponseEntity<?> respErr = controller.newPurchase(request, user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respErr.getStatusCode());
        verify(purchaseService, times(2)).savePurchase(user, request);
    }

}
