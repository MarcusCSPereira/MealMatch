package com.mealmatch.enums;

public enum DificuldadeEnum {
    FACIL(1),
    MEDIO(2),
    DIFICIL(3);

    private final int valor;

    DificuldadeEnum(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static DificuldadeEnum fromValor(int valor) {
        for (DificuldadeEnum dificuldade : values()) {
            if (dificuldade.getValor() == valor) {
                return dificuldade;
            }
        }
        throw new IllegalArgumentException("Valor de dificuldade inválido: " + valor);
    }
}
