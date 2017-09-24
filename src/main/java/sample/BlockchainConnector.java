package sample;

import com.sun.istack.internal.Nullable;
import model.Phase;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;
import sun.security.provider.SHA;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockchainConnector {
    private Web3j web3j;
    private Credentials credentials;

    public String getAddress() {
        return address;
    }

    private String address;

    public BlockchainConnector(String pswd, @Nullable String file) throws Exception{
        web3j = Web3j.build(new HttpService());
        if (file == null)
            credentials = Credentials.create(pswd);
        else
                credentials = WalletUtils.loadCredentials(pswd, file);
        address = credentials.getAddress();
    }

    public double getBalance() throws Exception{
//        address = credentials.getAddress();
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

//    public boolean checkValidData(String login, String psw, String address){
//        // TODO: send to blockchain and check valid
//        getPhases("",""); // because hardcode inside
//        return true;
//    }

    public void sendAccept() {
        // TODO: send to the contract
    }

    public void sendReject() {
        // TODO: send to the contract
    }

    public void sendAssessment(String btnName){
        switch (btnName){
            case "bad" :
                // TODO send to the contract
                System.out.println("Bad was pressed");
                break;
            case "well":
                // TODO send to the contract
                break;
            case "good":
                // TODO send to the contract
                break;
        }
    }

    public List<List<Phase>> getPhases(String contractAddress, String functionName){
        ArrayList<List<Phase>> allPhases = new ArrayList<>();
        ArrayList<Phase> newPhases = new ArrayList<>();
        ArrayList<Phase> historyPhases = new ArrayList<>();
        newPhases.add(new Phase(4,"New invoice!", "Marketting\nWe need a lot of resources",
                "100000",0, false, true, false));
        historyPhases.add(new Phase(1, "First phase", "Developing backend","50000",
                0, true, false, true));
        historyPhases.add(new Phase(3, "Third phase", "Developing IOS","100000",
                0,true, false, false));
        historyPhases.add(new Phase(2, "Second phase", "Developing web","70000",
                0.6, true, false, true));
        historyPhases.add(new Phase(0,"Zero phase", "Planning project","10000",
                0.6,true, false, true));

        allPhases.add(newPhases);
        allPhases.add(historyPhases);
        try {
           // List<Type> list = callFunc(contractAddress, functionName, new ArrayList(), new ArrayList());
            // iterate throw the list and get all new phases into one list and finished into annother list
        } catch (Exception e){
            e.printStackTrace();
        }
        return allPhases;
    }

    private List<Type> callFunc(String contractAddress,
                                     String functionName, List inputArgs, List outputs) throws Exception{
        // TODO: DELETE HARDCODE FUNCS AND ADDRESSES!!!!
        contractAddress = "0x395699a7e5a66f586d9debd06e4ddffbe57ffbad";
//        contractAddress = "0xa431fb52638fb43a5da01b0583961d895c2bb874";
        functionName = "getBalance";
        TransactionManager transactionManager = new RawTransactionManager(
                web3j, credentials, ChainId.MAIN_NET);
        inputArgs = new ArrayList();
        inputArgs.add(new Address("0xa431fb52638fb43a5da01b0583961d895c2bb874"));//"41b85c73a60830e40e0a4b5d1bffe5deff6ae919"));
//        inputArgs.add("41b85c73a60830e40e0a4b5d1bffe5deff6ae919");

        outputs.add(TypeReference.create(Uint.class));
        Function function = new  Function(functionName, inputArgs, outputs);
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = web3j.ethCall(Transaction.createEthCallTransaction(
                credentials.getEcKeyPair().getPrivateKey().toString(16),
                contractAddress, encodedFunction), DefaultBlockParameterName.LATEST).sendAsync().get();

        if(response.hasError()){
            System.out.println("functionCall: " + response.getError().getMessage());
        }
        List<Type> res = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        System.out.println(res);
        System.out.println("Balance: " + outputs.get(0).toString());
        return res;
    }

    private void convertMyResult(List<Type> returns, List<String> results) throws Exception {
        if (returns.size() > 0) {

            for(int i=0; i<returns.size(); i++){

                String res = returns.get(i).getValue().toString();
                results.add(res);
                System.out.println(res);
            }
        }
    }
}
