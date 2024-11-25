package datab;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import model.Metadata;
import model.MetadataExtractor;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

public class MusicDB {

    private BlobContainerClient containerClient;

    public MusicDB() {
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=musicappdb;AccountKey=/TxkG8DnJ6NGWCEnv/82FiqesEi04JLZ/s6qd5Ox78qGJuxETnxCrpVs6C42jsmTzNUQ65iZ5cLn+AStfJBFbw==;EndpointSuffix=core.windows.net")
                .containerName("media-files")
                .buildClient();
    }

    public void uploadFile(String filePath, String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(filePath);
    }

    public BlobContainerClient getContainerClient() {
        return containerClient;
    }
    public boolean doesBlobExist(String blobName) {
        for (BlobItem blobItem : this.containerClient.listBlobs()) {
            if (blobItem.getName().equals(blobName)) {
                return true;
            }
        }
        return false;
    }
    public String listAllBlobsMetadataOnly() {
        StringBuilder metadataBuilder = new StringBuilder();

        // Add column headers
        metadataBuilder.append(String.format("%-20s %-20s %-10s %-20s %-20s\n", "Song Name", "Artist", "Duration", "Album", "Genre"));
        metadataBuilder.append("-------------------------------------------------------------------------------------------\n");

        // Iterate over each blob item in the container
        for (BlobItem blobItem : containerClient.listBlobs()) {
            try {
                // Create a BlobClient for the current blob
                BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());

                // Use MetadataExtractor to extract metadata from the blob
                Metadata metadata = MetadataExtractor.extractMetadataDB(blobClient, blobItem.getName());

                // Append metadata row
                metadataBuilder.append(String.format("%-20s %-20s %-10s %-20s %-20s\n",
                        metadata.getSongName(), metadata.getArtist(), metadata.getDuration(),
                        metadata.getAlbum(), metadata.getGenre()));
            } catch (Exception e) {
                // Append an error row for this blob
                metadataBuilder.append(String.format("%-20s %-20s %-10s %-20s %-20s\n",
                        "Na", "Na", "Na", "Na", "Na"));
            }
        }

        return metadataBuilder.toString();
    }

    public void downloadFileWithProgress(String blobName, String destinationPath, BiConsumer<Long, Long> progressCallback) throws Exception {
        MusicDB musicDB = new MusicDB();

        // List available blobs to validate blobName
        boolean blobExists = false;
        for (BlobItem blobItem : musicDB.getContainerClient().listBlobs()) {
            System.out.println("Available blob: " + blobItem.getName());
            if (blobItem.getName().equals(blobName)) {
                blobExists = true;
            }
        }

        if (!blobExists) {
            throw new IllegalArgumentException("Blob not found in container: " + blobName);
        }

        BlobClient blobClient = musicDB.getContainerClient().getBlobClient(blobName);
        System.out.println("BlobClient initialized for: " + blobName);

        try (InputStream inputStream = blobClient.openInputStream();
             OutputStream outputStream = new FileOutputStream(destinationPath)) {

            long totalBytes = blobClient.getProperties().getBlobSize();
            long bytesDownloaded = 0;
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesDownloaded += bytesRead;

                // Update progress
                progressCallback.accept(bytesDownloaded, totalBytes);
            }

            System.out.println("Download complete: " + blobName);

        } catch (BlobStorageException e) {
            if (e.getStatusCode() == 404) {
                System.err.println("Blob not found: " + blobName);
            } else {
                System.err.println("Error during blob download: " + e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during download: " + e.getMessage());
            throw e;
        }
    }


}
