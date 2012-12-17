package org.jchecksum;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class allows to calculate checksum of a file.
 */
public class JChecksum {
    private static final int BUFFER_SIZE = 256 * 1024;

    /**
     * Indicates the size of the buffer to use when reading files. You may override this method in case the default
     * buffer size (256K) doesn't suit you.
     *
     * @return the size of the buffer.
     */
    public int withBufferSizeOf() {
        return BUFFER_SIZE;
    }

    private MessageDigest getMessageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public final String sha1(String file) throws IOException {
        return hash(file, getMessageDigest("SHA-1"));
    }

    public final String hash(String file, MessageDigest messageDigest) throws IOException {
        final Path path = Paths.get(file);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(withBufferSizeOf());

        try (final ByteChannel byteChannel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            int read;
            while ((read = byteChannel.read(buffer)) > 0) {
                buffer.limit(read);
                messageDigest.update(buffer);
                buffer.clear();
            }
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final byte b : messageDigest.digest()) {
            stringBuilder.append(String.format("%xd", b));
        }

        return stringBuilder.toString();
    }
}
