import java.util.ArrayList;
import java.util.Optional;
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
        ArrayList<Barbeiro> barbeiros = this.barbearia.getBarbeiros();
        Queue fila = this.barbearia.getFila();

        try{
            if(sBarbeiros.availablePermits() > 0 & sFila.availablePermits() > 0) {
                sBarbeiros.acquire();
                synchronized (this) {
                    for (Barbeiro barbeiro1 : barbeiros) {
                        if (barbeiro1.getCliente() == null) {
                            barbeiro = barbeiro1;
                            break;
                        }
                    }
                    if(barbeiro != null) {
                        barbeiro.setCliente(this);
                        barbeiro.start();
                    }
                    else {
                        if(fila.offer(this)){
                            sFila.acquire();
                            System.out.println("> cliente " + this.getId() + " se sentou");
                            this.interrupt();
                        } else {
                            System.out.println("> cliente " + this.getId() + " foi embora");
                            this.interrupt();
                        }
                    }
                }
                sBarbeiros.release();
            } else {
                System.out.println("> cliente " + this.getId() + " esta indo embora");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(Cliente o) {
        return Integer.compare(this.id, o.id);
    }
}