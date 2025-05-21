package org.example.Objetos;

public class Onda {
    private double frequencia, comprimentoOnda;

    public Onda() {
    }

    public Onda(double frequencia, double comprimentoOnda) {
        this.frequencia = frequencia;
        this.comprimentoOnda = comprimentoOnda;
    }

    public double getFrequencia() {
        return frequencia;
    }
    public void setFrequencia(double frequencia) {
        this.frequencia = frequencia;
    }
    public double getComprimentoOnda() {
        return comprimentoOnda;
    }
    public void setComprimentoOnda(double comprimentoOnda) {
        this.comprimentoOnda = comprimentoOnda;
    }
}
