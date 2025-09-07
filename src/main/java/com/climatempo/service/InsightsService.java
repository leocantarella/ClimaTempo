package com.climatempo.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class InsightsService {

    // Conforto Térmico
    public String calcularConfortoTermico(double tempC, int umidade) {
        if (tempC > 32 && umidade > 60) return "Muito quente e úmido, risco de mal-estar.";
        else if (tempC < 15) return "Clima frio, vista-se adequadamente.";
        else if (tempC >= 28 && umidade >= 40) return "Desconforto leve por conta do calor.";
        else return "Clima agradável.";
    }

    //Recomendação de Hidratação
    public String recomendacaoHidratacao(double tempC) {
        if (tempC > 30) return "Recomenda-se beber pelo menos 3 litros de água hoje.";
        else if (tempC >= 20) return "Mantenha a hidratação: cerca de 2 litros de água para hoje.";
        else return "Com o clima fresco, 1,5 litro de água é recomendado para hoje.";
    }

    // Alerta de vento
    public String alertaDeVento(double windSpeedMs) {
        if (Double.isNaN(windSpeedMs)) return "Vento: informação indisponível.";
        if (windSpeedMs > 10.8) {
            return "Vento forte — cuidado com objetos soltos e estruturas leves.";
        } else if (windSpeedMs > 5.5) {
            return "Vento moderado — possível desconforto em áreas abertas.";
        } else {
            return "Vento leve — condições estáveis.";
        }
    }

    // Melhores horários do dia para sair
    public String melhoresHorariosDoDia(double tempC, long nascerSol, long porSol, Integer timezoneShiftSeconds) {
        if (nascerSol <= 0 || porSol <= 0 || timezoneShiftSeconds == null) {
            return "Horários recomendados: informação indisponível.";
        }

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneShiftSeconds);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime sunrise = LocalDateTime.ofEpochSecond(nascerSol, 0, offset);
        LocalDateTime sunset  = LocalDateTime.ofEpochSecond(porSol, 0, offset);

        if (tempC >= 30.0) {
            LocalDateTime manhaLimite = sunrise.plusHours(2);
            LocalDateTime tardeInicio = sunset.minusHours(2);
            return "Prefira sair antes de " + fmt.format(manhaLimite) + " e após " + fmt.format(tardeInicio) + ".";
        } else {
            return "Qualquer horário do dia está adequado.";
        }
    }
}
