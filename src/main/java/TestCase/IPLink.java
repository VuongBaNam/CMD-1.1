package TestCase;

import java.util.Objects;

public class IPLink extends Object{
    String IP;
    String link;

    public IPLink(String IP, String link) {
        this.IP = IP;
        this.link = link;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof IPLink)) return false;
        IPLink ipLink = (IPLink)o;
        return Objects.equals(IP,ipLink.getIP()) && Objects.equals(link,ipLink.getLink());

    }

    @Override
    public int hashCode(){
        return Objects.hash(IP,link);
    }
}
