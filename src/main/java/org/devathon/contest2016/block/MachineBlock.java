package org.devathon.contest2016.block;

import java.io.Serializable;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block
 */
public interface MachineBlock extends Serializable {

    void load();

    BlockType type();

}
