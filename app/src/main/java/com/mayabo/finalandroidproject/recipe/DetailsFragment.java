//package com.mayabo.finalandroidproject.recipe;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//
//public class DetailsFragment extends Fragment {
//
//    private boolean isTablet;
//    private Bundle dataFromActivity;
//    private long id;
//
//    public void setTablet(boolean tablet) { isTablet = tablet; }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        //get what is transfer from the setArguments
//        dataFromActivity = getArguments();
//        //get the ID from the key that is set in FragmentExample
//        id = dataFromActivity.getLong(ChatRoomActivity.ITEM_ID );
//
//        // Inflate the layout for this fragment
//        View result =  inflater.inflate(R.layout.fragment_detail, container, false);
//
//        //show who send
//        TextView whoSend = (TextView)result.findViewById(R.id.who_send);
//        boolean checkWho = dataFromActivity.getBoolean(ChatRoomActivity.WHO_SEND);
//
//        if (checkWho) {
//            whoSend.setText("Messge is sent from: Sender");
//        } else {
//            whoSend.setText("Messge is sent from: Receiver");
//        }
//
//
//        //show the message
//        TextView message = (TextView)result.findViewById(R.id.message);
//        message.setText("Message is : "+dataFromActivity.getString(ChatRoomActivity.ITEM_SELECTED));
//
//        //show the id:
//        TextView idView = (TextView)result.findViewById(R.id.idText);
//        idView.setText("ID= " + id);
//
//
//        //Show ID represent in database
//        TextView idDatabase = (TextView)result.findViewById(R.id.idDatabase);
//        idDatabase.setText("ID of message in database = " + dataFromActivity.getString(ChatRoomActivity.ID_IN_DB));
//
//
//
//
//
//
//        // get the delete button, and add a click listener:
//        Button deleteButton = (Button)result.findViewById(R.id.deleteButton);
//        deleteButton.setOnClickListener( clk -> {
//
//            if(isTablet) { //both the list and details are on the screen:
//                ChatRoomActivity parent = (ChatRoomActivity) getActivity();
//                parent.deleteMessageId((int)id); //this deletes the item and updates the list
//
//                //now remove the fragment since you deleted it from the database:
//                // this is the object to be removed, so remove(this):
//
//
//                parent.getSupportFragmentManager()//get the fragment manager
//                        .beginTransaction()//start the transaction
//                        .remove(this)//call remove action
//                        .commit();// action transaction commit from the remove action
//            }
//            //for Phone:
//            else //You are only looking at the details, you need to go back to the previous list page
//            {
//                EmptyActivity parent = (EmptyActivity) getActivity();
//                Intent backToFragmentExample = new Intent();
//                backToFragmentExample.putExtra(ChatRoomActivity.ITEM_ID, dataFromActivity.getLong(ChatRoomActivity.ITEM_ID ));
//                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
//                parent.finish(); //go back
//            }
//        });
//        return result;
//    }
//
//
//}
