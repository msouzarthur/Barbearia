import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Barbearia extends Thread {

    private Semaphore sBarbeiros;
    private Semaphore sFila;
    private Semaphore sPentes;
    private Semaphore sTesouras;
    private final ArrayList<Barbeiro> barbeiros;
    private final ArrayList<Thread> threads = new ArrayList<>();
    private Queue fila;
    private int nFila;

    public Barbearia(int nBarbeiros, int nFila) {
        this.sBarbeiros = new Semaphore(nBarbeiros);
        this.sFila = new Semaphore(nFila);
        this.barbeiros = new ArrayList<Barbeiro>(nBarbeiros);
        this.sPentes = new Semaphore(nBarbeiros/2);
        this.sTesouras = new Semaphore(nBarbeiros/2);
        this.fila = new PriorityQueue(nFila);
        this.nFila = nFila;
        for (int i = 0; i < 10; i++) {
            barbeiros.add(new Barbeiro(i, sPentes, sTesouras, this));
        }
    }

    @Override
    public void run() {
        int count = 0;
        while (count < 20) {
            try {
                Thread.sleep(200l);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Cliente cliente = new Cliente(count, this);
            threads.add(cliente);
            cliente.start();
            count++;
        }
        for (Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException ex){
                System.out.println(ex);
            }
        }
    }

    public ArrayList<Barbeiro> getBarbeiros() {
        return barbeiros;
    }

    public Semaphore getsBarbeiros() {
        return sBarbeiros;
    }

    public Semaphore getsFila() {
        return sFila;
    }

    public Queue getFila() {
        return fila;
    }

    public int getFilaSize() {
        return nFila;
    }
}