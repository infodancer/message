package org.infodancer.message.transport;

public class MXComparator implements java.util.Comparator<MX>
{
    public int compare(MX o1, MX o2)
    {
            if ((o1 != null) && (o2 != null))
            {
                    return o1.priority - o2.priority;
            }
            else if ((o1 == null) && (o2 != null)) return -1;
            else if ((o1 == null) && (o2 != null)) return 1;
            else return 0;
    }
    
    public boolean equals(Object o)
    {
            if (o instanceof MXComparator) return true;
            else return false;
    }

}
