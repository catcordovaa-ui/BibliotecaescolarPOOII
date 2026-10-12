
package concurrencia;

import modelo.Prestamo;
import servicio.PrestamoServicio;

public class HiloPrestamo extends Thread {

    private Prestamo prestamo;
    private PrestamoServicio prestamoServicio;
    private boolean resultado;

    public HiloPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
        this.prestamoServicio = new PrestamoServicio();
        this.resultado = false;
    }

    @Override
    public void run() {
        resultado = prestamoServicio.registrarPrestamo(prestamo);
    }

    public boolean isResultado() {
        return resultado;
    }
}

