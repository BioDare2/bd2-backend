
package ed.biodare2.backend.util.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for packing and handling zip archives
 * @author tzielins
 */
public class ZipMaker {


    /**
     * Packs into zip archive given files. The archive is saved into the 'out' file, the files in the archive
     * are stored under names taken from the map, so instead of file.getName() the key for that file in the map is taken
     * @param files map of pairs file_name, file, where file_name denotes new for that file in the produced zip archive
     * @param out file into which the archive is going to be saved
     * @throws IOException in case of IOErrors
     */
    public void packFiles(Map<String,Path> files,Path out) throws IOException {


	try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(out))) {
            
	    for(String fName : files.keySet()) {
		addFile(files.get(fName),fName,zos);
	    }
	} 
    }

    /**
     * Packs into zip archive given files. The archive is saved into the 'out' file, the files in the archive
     * are stored under their current names
     * @param files List of files to be packed
     * @param out file into which the archive is going to be saved
     * @throws IOException in case of IOErrors
     */
    public void packFiles(List<Path> files,Path out) throws IOException {

	Map<String,Path> names_map = new HashMap<>();
        
	for (Path file : files)
	    names_map.put(file.getFileName().toString(), file);

	packFiles(names_map,out);
    }

    protected void addFile(Path file, String fName, ZipOutputStream zos) throws IOException {

        ZipEntry entry = new ZipEntry(fName);
        zos.putNextEntry(entry);
        Files.copy(file, zos);
        zos.closeEntry();

    }

}
