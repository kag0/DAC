package blackdoor.cqbe.addressing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import blackdoor.crypto.Hash;
import blackdoor.util.Misc;

/**
 * Represents a file as a value stored locally, which is retrievable by a given
 * key. The key to retrieve is the hash of the file.
 * <p>
 * 
 * @author Nathan Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class CASFileAddress extends FileAddress {

	public static final int FILE_NAME_LEN;
	static {
		FILE_NAME_LEN = Math.min(89, Address.DEFAULT_ADDRESS_SIZE * 3 - 1);

	}

	public CASFileAddress(File f) throws IOException {
		super.f = f;
		setOverlayAddress(Hash.getFileHash(f));
	}

	/**
	 * Creates a new file with the data in bin if one does not already exist.
	 * Throws an exception if the file does already exist
	 * <p>
	 * 
	 * @param f
	 * @param bin
	 * @throws IOException
	 */
	public CASFileAddress(File f, byte[] bin) throws IOException {
		Files.write(f.toPath(), bin, StandardOpenOption.CREATE_NEW,
				StandardOpenOption.WRITE);
		super.f = f;
		setOverlayAddress(Hash.getFileHash(f));
	}

	/**
	 * Writes binary to a file in folder.
	 * <p>
	 * 
	 * @param folder
	 *            the folder to save bin to
	 * @param bin
	 * @throws IOException
	 */
	public CASFileAddress(Path folder, byte[] bin) throws IOException {
		super.setOverlayAddress(Hash.getSHA256(bin, false));
		File f = new File(folder.toFile(), Misc.getHexBytes(
				getOverlayAddress(), "_").substring(0, FILE_NAME_LEN));
		Files.write(f.toPath(), bin, StandardOpenOption.CREATE,
				StandardOpenOption.WRITE);
		super.setFile(f);
	}

	/**
	 * Get the binary of the file referenced by this object.
	 * <p>
	 * Don't be stupid and try to read big files into memory.
	 * 
	 * @return file binary
	 * 
	 * @throws IOException
	 */
	public byte[] getBinary() throws IOException {
		return Files.readAllBytes(getFile().toPath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CASFileAddress [address=" + overlayAddressToString()
				+ ", file=" + f.getAbsolutePath() + "]";
	}

}
