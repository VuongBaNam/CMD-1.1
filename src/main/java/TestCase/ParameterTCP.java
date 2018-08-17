package TestCase;

public class ParameterTCP {
    private String ip;
    private double number_port;
    private double entropy_port_src;

    public ParameterTCP(String ip, double number_port, double entropy_port_src) {
        this.ip = ip;
        this.number_port = number_port;
        this.entropy_port_src = entropy_port_src;
    }

    public double getNumber_port() {
        return number_port;
    }

    public void setNumber_port(double number_port) {
        this.number_port = number_port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public double getEntropy_port_src() {
        return entropy_port_src;
    }

    public void setEntropy_port_src(double entropy_port_src) {
        this.entropy_port_src = entropy_port_src;
    }
}
