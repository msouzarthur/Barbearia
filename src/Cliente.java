import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Cliente extends Thread implements Comparable<Cliente>{
    private int id;
    private int tempoCorte;
    private Barbearia barbearia;

    public Cliente(int id, Barbearia barbearia) {
        this.id = id;
        this.tempoCorte = ThreadLocalRandom.current().nextInt(3, 6);
        this.barbearia = barbearia;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTempoCorte() {
        return tempoCorte;
    }

    public void run(){
        System.out.println("> cliente " + id + " entrou <"+this.tempoCorte+"s>");
        Semaphore sBarbeiros = this.barbearia.getsBarbeiros();
        Semaphore sFila = this.barbearia.getsFila();
        Barbeiro barbeiro = null;
        Queue fila = this.barbearia.getFila();

        try{
            if(sBarbeiros.availablePermits() > 0) {
                sBarbeiros.acquire();
                synchronized (this) {
                    barbeiro = barbearia.getBarbeiroLivre();
                    if(barbeiro != null) {
                        Thread threadX = new Thread(barbeiro);
                        barbeiro.setCliente(this);
                        threadX.start();
                        threadX.join();
                    }
                }
                sBarbeiros.release();
            } else {
                if (sFila.availablePermits() > 0){
                    fila.add(this);
                    sFila.acquire();
                    System.out.println("> cliente " + this.getId() + " se sentou");
                    this.interrupt();
                } else {
                    System.out.println("> cliente " + this.getId() + " foi embora");
                    synchronized (this) {
                        barbearia.desistente();
                    }
                    this.interrupt();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(Cliente o) {
        return Integer.compare(this.id, o.id);
    }
}