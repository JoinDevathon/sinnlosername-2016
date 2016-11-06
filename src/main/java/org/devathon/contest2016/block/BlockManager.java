package org.devathon.contest2016.block;

import org.bukkit.Location;
import org.devathon.contest2016.DevathonPlugin;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block
 */
public class BlockManager implements Serializable {

    private static BlockManager instance;
    private HashMap<SerializeableLocation, MachineBlock> blocks = new HashMap<>();

    public static BlockManager getInstance() {
        return instance;
    }

    public static void load() {

        final File dataFile = new File(DevathonPlugin.helper().plugin().getDataFolder(), "data");

        if (dataFile.exists()) {

            try {
                final FileInputStream fileIn = new FileInputStream(dataFile);
                final ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                instance = (BlockManager) objectIn.readObject();

                objectIn.close();

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Could not load BlockManager instance :(");
                e.printStackTrace();
            }

        } else
            instance = new BlockManager();

        getInstance().getBlocks().forEach((l, mb) -> mb.load(l.toLocation()));

    }

    public static void save() {

        final File dataFolder = DevathonPlugin.helper().plugin().getDataFolder();
        final File dataFile = new File(dataFolder, "data");

        try {
            if (!dataFolder.exists())
                dataFolder.mkdir();

            if (!dataFile.exists())
                dataFile.createNewFile();

            getInstance().getBlocks().forEach((l, mb) -> mb.serialize());

            final FileOutputStream fileOut = new FileOutputStream(dataFile);
            final ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(instance);

            objectOut.flush();
            objectOut.close();

        } catch (IOException e) {
            System.out.println("Could not save BlockManager instance :(");
            e.printStackTrace();
        }

    }

    public HashMap<SerializeableLocation, MachineBlock> getBlocks() {
        return blocks;
    }

    public MachineBlock addBlock(Location location, MachineBlock block) {
        blocks.put(new SerializeableLocation(location), block);
        return block;
    }

    public void remove(Location location) {
        blocks.remove(new SerializeableLocation(location));
    }


}