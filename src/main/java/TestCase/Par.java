package TestCase;

public class Par {
    private double IAT;
    private double PpF;
    private String MaxRateProtol;

    public Par(double IAT, double ppF, String maxRateProtol) {
        this.IAT = IAT;
        PpF = ppF;
        MaxRateProtol = maxRateProtol;
    }

    public double getIAT() {
        return IAT;
    }

    public void setIAT(double IAT) {
        this.IAT = IAT;
    }

    public double getPpF() {
        return PpF;
    }

    public void setPpF(double ppF) {
        PpF = ppF;
    }

    public String getMaxRateProtol() {
        return MaxRateProtol;
    }

    public void setMaxRateProtol(String maxRateProtol) {
        MaxRateProtol = maxRateProtol;
    }
}
