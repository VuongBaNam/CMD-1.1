package TestCase;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Nam on 17/04/2017.
 */
public class Item implements Serializable{
    private Map<String, Object> fields = new HashMap<String, Object>();

    public Map<String, Object> getFields() {
        return Collections.unmodifiableMap(this.fields);
    }

    public Item setAttribute(String name, Object value) {
        this.fields.put(name, value);
        return this;
    }

    public Set<String> getAttributeNames() {
        return this.fields.keySet();
    }

    public Object getFieldValue(String attributeName) {
        return this.fields.get(attributeName);
    }

    public boolean hasAttribute(String attributeName) {
        return this.fields.get(attributeName) != null;
    }
}
