package com.mimmarcelo.chatbluetooth.bluetooth;

/**
 * PERMITE A COMUNICAÇÃO ENTRE A ACTIVITY E A THREAD DE CONEXÃO
 */
public interface IBluetoothListener {

	/**
	 * RECEBE A MENSAGEM RECEBIDA ATRAVÉS DA THREAD DE CONEXÃO
	 * @param message MESAGEM RECEBIDA
	 */
	void bluetoothMessageReceived(Message message);
}
