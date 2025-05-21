package org.example.Cálculos;

import org.example.Objetos.Onda;

public class Calculos {

    Onda obj_onda = new Onda();

    private double comp_onda;
    private double epsilon;
    private double frequency;

    public Calculos() {
    }

    void init_vars() {
        if (obj_onda == null) {
            throw new IllegalStateException("O objeto Onda não foi inicializado.");
        }
        this.comp_onda = Calculos.convert.cm_m(obj_onda.getComprimentoOnda());
        this.frequency = obj_onda.getFrequencia();
    }

    public double calcularVelocidade(double freq, double comp) {
        return freq * comp;
    }

    public static double normalizeAngle(double x) {
        while (x > Math.PI) x -= 2 * Math.PI;
        while (x < -Math.PI) x += 2 * Math.PI;
        return x;
    }

    public static class convert {

        public static double m_cm(double x) {
            return x * 100;
        }

        public static double cm_m(double x) {
            return x / 100;
        }
    }

    public class SenoTaylor {

        public static double calcularSeno(double x, double erroMaximo) {
            // Converte o erro máximo do usuário para o valor correspondente
            double erro = Math.pow(10, -erroMaximo);

            // Normaliza x para o intervalo [-π, π]
            x = x % (2 * Math.PI);
            if (x > Math.PI) x -= 2 * Math.PI;
            else if (x < -Math.PI) x += 2 * Math.PI;

            double termo = x;
            double soma = termo;
            int n = 1;

            while (Math.abs(termo) > erro) {
                // Calcula o próximo termo: (-1)^n * x^(2n+1) / (2n+1)!
                termo *= -x * x / ((2 * n) * (2 * n + 1));
                soma += termo;
                n++;
            }

            return soma;
        }
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getComp_onda() {
        return comp_onda;
    }

    public void setComp_onda(double comp_onda) {
        this.comp_onda = comp_onda;
    }
}
