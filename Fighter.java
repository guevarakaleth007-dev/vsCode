public class Fighter {
    protected String name;
    protected int hp;
    protected int maxHp;

    public Fighter(String name, int hp) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
    }

    // Aplica daño y muestra resultado en consola del servidor
    public synchronized void takeDamage(int amount, String attacker) {
        if (!isAlive()) return;

        hp -= amount;
        if (hp < 0) hp = 0;

        // Barra de vida visual (20 segmentos)
        StringBuilder bar = new StringBuilder("[");
        int total = 20;
        int filled = (int) Math.round(((double) hp / maxHp) * total);
        for (int i = 0; i < total; i++) {
            bar.append(i < filled ? '=' : ' ');
        }
        bar.append("]");

        System.out.println(attacker + " golpea a " + name + " por " + amount + " de daño.");
        System.out.println("-> Vida de " + name + ": " + hp + "/" + maxHp + " " + bar);
    }

    public synchronized boolean isAlive() {
        return hp > 0;
    }

    public synchronized int getHp() {
        return hp;
    }

    public String getName() {
        return name;
    }
}

