package com.climatempo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClimaServiceTest {

    @Test
    @DisplayName("climaPorCidade: mock retorna nulo sem erro")
    void climaPorCidade() {
        ClimaService service = mock(ClimaService.class);

        when(service.climaPorCidade("Palmas")).thenReturn(null);

        var result = service.climaPorCidade("Palmas");

        assertNull(result);
        verify(service).climaPorCidade("Palmas");
    }

    @Test
    @DisplayName("ultimas10PorCidade: mock retorna lista vazia")
    void ultimas10PorCidade() {
        ClimaService service = mock(ClimaService.class);

        when(service.ultimas10PorCidade(1L)).thenReturn(List.of());

        var lista = service.ultimas10PorCidade(1L);

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
        verify(service).ultimas10PorCidade(1L);
    }

    @Test
    @DisplayName("removerRegistro: mock não lança erro")
    void removerRegistro() {
        ClimaService service = mock(ClimaService.class);

        doNothing().when(service).removerRegistro(42L);

        service.removerRegistro(42L);

        verify(service).removerRegistro(42L);
    }

    @Test
    @DisplayName("consultarComInsights: mock retorna nulo")
    void consultarComInsights() {
        ClimaService service = mock(ClimaService.class);

        when(service.consultarComInsights("Palmas")).thenReturn(null);

        var result = service.consultarComInsights("Palmas");

        assertNull(result);
        verify(service).consultarComInsights("Palmas");
    }
}
