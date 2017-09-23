package sample;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BlockchainConnector {
    private Web3j web3j;
    private Credentials credentials;

    public BlockchainConnector(String pswd){
        web3j = Web3j.build(new HttpService());
        credentials = Credentials.create(pswd);
    }

    public double getBalance() throws Exception{
        String address = credentials.getAddress();
        EthGetBalance ethGetBalance = web3j
                .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();

        BigInteger wei = ethGetBalance.getBalance();
        System.out.print("Wei: " + wei);
        BigDecimal balanceDecimal = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
        System.out.println("\tConverted: " +balanceDecimal);
        return balanceDecimal.doubleValue();
    }
}
