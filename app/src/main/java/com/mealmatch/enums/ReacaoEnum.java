package com.mealmatch.enums;

public enum ReacaoEnum {
    LIKE(2),
    DISLIKE(1),
    NULO(0);

    private final int valor;

    ReacaoEnum(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static ReacaoEnum fromValor(int valor) {
        for (ReacaoEnum reacao : values()) {
            if (reacao.getValor() == valor) {
                return reacao;
            }
        }
        throw new IllegalArgumentException("Valor de dificuldade inválido: " + valor);
    }
}