import java.util.concurrent.Semaphore;

public class Barbeiro extends Thread {
    private int id;
    private Semaphore sPentes;
    private Semaphore sTesouras;
    private Cliente cliente;
    private Barbearia barbearia;

    public Barbeiro(int id, Semaphore sPentes, Semaphore sTesouras, Barbearia barbearia){
        this.id = id;
        this.sPentes = sPentes;
        this.sTesouras = sTesouras;
        this.barbearia = barbearia;
        this.cliente = null;
    }
    public long getId(){return this.id;}
    public void setCliente(Cliente cliente) {this.cliente = cliente;}
    public Cliente getCliente() {
        return cliente;
    }
    public void run(){
        Semaphore sFila = this.barbearia.getsFila();
        System.out.println("> barbeiro "+this.id+" acordou");

        if (this.cliente == null){
            System.out.println("> barbeiro " + this.id + " dormiu");
        }
        else {
            System.out.println("> barbeiro "+this.id+" esta atras de tesoura e pente");
            while(this.cliente != null) {
                try {
                    this.sPentes.acquire();
                    this.sTesouras.acquire();
                    System.out.println("> barbeiro " + this.id + " conseguiu tesoura e pente");
                    System.out.println("> barbeiro " + this.id + " iniciou o corte do cliente " + this.cliente.getId() + " <" + this.cliente.getTempoCorte() + "s>");
                    Thread.sleep(this.cliente.getTempoCorte() * 1000);
                    System.out.println("> barbeiro " + this.id + " terminou o corte do cliente " + this.cliente.getId());
                    this.cliente = null;
                    this.sPentes.release();
                    this.sTesouras.release();
                    barbearia.corte();
                    if (barbearia.getFila().size() > 0) {
                        this.cliente = (Cliente) barbearia.getFila().poll();
                        sFila.release();
                        System.out.println("> barbeiro " + this.id + " chamou o cliente " + cliente.getId());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("> barbeiro "+this.id+" foi dormir");
        }
    }
}
