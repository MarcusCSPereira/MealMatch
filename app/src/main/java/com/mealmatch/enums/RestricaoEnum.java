package com.mealmatch.enums;

public enum RestricaoEnum {
    FRUTOS_DO_MAR(1),
    VEGANA(2),
    VEGETARIANA(3),
    ACUCAR(4),
    LACTOSE(5),
    GLUTEN(6);


    private final int valor;

    RestricaoEnum(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static RestricaoEnum fromValor(int valor) {
        for (RestricaoEnum restricao : values()) {
            if (restricao.getValor() == valor) {
                return restricao;
            }
        }
        throw new IllegalArgumentException("Valor de restricao inválido: " + valor);
    }
}
