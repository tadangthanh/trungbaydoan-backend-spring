package vnua.edu.xdptpm09.configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfig {
    @Value("${azure.blob-storage.connection-string}")
    private String connectionString;

    public AzureBlobConfig() {
    }

    @Bean
    public BlobServiceClient blobServiceClient() {
        return (new BlobServiceClientBuilder()).connectionString(this.connectionString).buildClient();
    }
}
