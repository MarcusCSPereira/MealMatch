package com.mealmatch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class ImageSelector {

  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // Limite de 5 MB

  public static File selecionarImagem(Window window) {
    // Cria uma instância de FileChooser que permite a seleção de arquivos
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Selecione uma imagem");

    // Define as extensões permitidas, logo arquivos com extensões diferentes não serão "clicáveis"
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

    // Exibe o seletor de arquivos
    File selectedFile = fileChooser.showOpenDialog(window);

    if (selectedFile != null) {
      
      // Valida o tamanho do arquivo
      if (selectedFile.length() > MAX_FILE_SIZE) {
        exibirAlerta("Tamanho excedido", "O arquivo selecionado excede o tamanho máximo permitido de 5 MB.");
        return null;
      }

      // Valida a extensão do arquivo
      String fileName = selectedFile.getName().toLowerCase();
      if (!fileName.endsWith(".png") && !fileName.endsWith(".jpg") &&
          !fileName.endsWith(".jpeg") && !fileName.endsWith(".gif") && !fileName.endsWith(".bmp")) {
        exibirAlerta("Extensão inválida", "O arquivo selecionado não possui uma extensão de imagem válida.");
        return null;
      }

      // Retorna o arquivo válido
      return selectedFile;
    }

    return null;
  }

  public static Image carregarImagem(File file) {
    return new Image(file.toURI().toString());
  }

  public static byte[] converterImagemParaBytes(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      // Cria um array de bytes do tamanho do arquivo
      byte[] bytes = new byte[(int) file.length()];
      // Lê os bytes do arquivo para o array
      fis.read(bytes);
      return bytes;
    }
  }

  private static void exibirAlerta(String titulo, String mensagem) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensagem);
    alert.showAndWait();
  }
}