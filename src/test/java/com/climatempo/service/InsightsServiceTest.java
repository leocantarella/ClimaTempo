package com.climatempo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class InsightsServiceTest {

    private final InsightsService service = new InsightsService();

    // Hidratação
    //  - "Mantenha a hidratação: cerca de 2 litros de água para hoje."
    //  - "Recomenda-se beber pelo menos 3 litros de água hoje."
    // Vamos validar por palavras-chave e, quando aplicável, extrair número de litros.
    private static Integer extrairLitros(String texto) {
        Pattern p = Pattern.compile("(\\d+)\\s*litro");
        Matcher m = p.matcher(texto);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    @DisplayName("recomendacaoHidratacao: deve retornar mensagem coerente e litros esperados quando aplicável")
    @ParameterizedTest(name = "[{index}] temp={0}, umidade={1}%")
    @CsvSource({
            // temp, humidity, litrosEsperadosOuZeroSeNaoSeAplica, precisaConter
            "25.0, 40, 2, Mantenha a hidratação",          // Moderado: ~2 litros
            "33.0, 25, 3, Recomenda-se beber pelo menos",  // Calor/umidade baixa: >= 3 litros
            "20.0, 80, 2, Mantenha a hidratação"           // Umidade alta sem calor extremo: mensagem padrão ~2L
    })
    void recomendacaoHidratacao(double temp, int humidity, int litrosEsperados, String trechoEsperado) {
        String resultado = service.recomendacaoHidratacao(temp);
        assertNotNull(resultado);
        assertTrue(resultado.contains(trechoEsperado),
                () -> "Mensagem não contém o trecho esperado. Esperado: \"" + trechoEsperado + "\"; obtido: " + resultado);

        Integer litros = extrairLitros(resultado);
        assertNotNull(litros, () -> "Não foi possível extrair quantidade de litros da mensagem: " + resultado);
        assertEquals(litrosEsperados, litros.intValue(),
                () -> "Litros esperados = " + litrosEsperados + ", mas foi: " + litros + " | Mensagem: " + resultado);
    }

    // Vento
    @Nested
    @DisplayName("alertaDeVento")
    class AlertaDeVento {

        @Test
        @DisplayName("vento < 5 m/s => começa com 'Vento leve'")
        void ventoNormal() {
            String r = service.alertaDeVento(3.0);
            assertNotNull(r);
            assertTrue(r.startsWith("Vento leve"),
                    () -> "Esperava começar com 'Vento leve', mas foi: " + r);
        }

        @Test
        @DisplayName("5–10 m/s => começa com 'Vento moderado'")
        void ventoAtencao() {
            String r = service.alertaDeVento(7.0);
            assertNotNull(r);
            assertTrue(r.startsWith("Vento moderado"),
                    () -> "Esperava começar com 'Vento moderado', mas foi: " + r);
        }

        @Test
        @DisplayName("> 10 m/s => começa com 'Vento forte'")
        void ventoForte() {
            String r = service.alertaDeVento(12.0);
            assertNotNull(r);
            assertTrue(r.startsWith("Vento forte"),
                    () -> "Esperava começar com 'Vento forte', mas foi: " + r);
        }
    }
    }

