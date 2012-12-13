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
    public static void main(String[] args) {
        try {
            System.out.println(new JChecksum().getChecksum(args[0], Integer.valueOf(args[1])));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getChecksum(String filePath, int size) throws IOException, NoSuchAlgorithmException {
        final Path path = Paths.get(filePath);
        final ByteBuffer buffer = ByteBuffer.allocate(size);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        try (ByteChannel byteChannel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            int read = 0;
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
