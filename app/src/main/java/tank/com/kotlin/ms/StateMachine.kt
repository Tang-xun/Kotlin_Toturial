package tank.com.kotlin.ms

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log


/**
 *  @author: vancetang
 *  @date:   2019/5/3 10:15 PM
 */
class StateMachine {/*

    var mName: String? = null

    private lateinit var mSmThread: HandlerThread

    private lateinit var mSmHandler: SmHandler

    private fun initStateMechine(name: String, looper: Looper) {
        mName = name
//        mSmHandler = SmHandler(looper, this)
    }

    constructor(name: String) {
        mSmThread = HandlerThread(name)
        mSmThread.start()
        initStateMechine(name, mSmThread.looper)
    }

    constructor(name: String, looper: Looper) {
        initStateMechine(name, looper)
    }

    constructor(name: String, handler: SmHandler) {
        initStateMechine(name, handler.looper)
    }

    fun onPreHandlerMesssage(msg:Message?) {}

    fun onPostHandlerMessage(msg:Message?) {}

    fun addState(state:State, parent:State) {
        mSmHandler.addState(state, parent)
    }

    fun addState(state:State) {
        mSmHandler.addState(state, null)
    }



    *//**
     * sate machine handler class
     *//*
    class SmHandler : Handler() {

        *//** state machine has quit *//*
        private var mHasQuit: Boolean = false

        *//** state machine is debug model *//*
        private var isDebug: Boolean = false

        *//** current state message *//*
        private var mMsg: Message? = null

        *//** true if construction of the state machine has not been completed *//*
        private var isConstructionCompleted: Boolean = false

        *//** struck used to manage the current hierarchy of states *//*
        private var mStateStruck = arrayListOf<StateInfo>()

        *//** Top of mStateStack *//*
        private var mStateStackTopIndex = -1

        *//** a temporary struck used to manage the states struck  *//*
        private var mTempStateStruck = arrayListOf<StateInfo>()

        private var mSm: StateMachine? = null

        *//** The map of all of the states in the state machine *//*
        private var mStateInfo: Map<State, StateInfo> = mutableMapOf()

        private var mInitialState: State? = null

        private var mDestState: State? = null

        private var mTransitionInProgress: Boolean = false

        private var mIsContructionCompleted: Boolean = false

        private val mSmHandlerObj: Any? = Any()

        *//** The list of deferred messages *//*
        private var mDeferredMessages = arrayListOf<Message>()


        private class StateInfo {

            var state: State? = null

            var mParentState: StateInfo? = null

            var active = false

            override fun toString(): String {
                return "StateInfo(mState=${state?.getName()}, mParentState=${mParentState?.state?.getName()}, active=$active)"
            }

        }

        inner class HaltingState : State() {

            override fun processMessage(msg: Message?): Boolean? {

                return true
            }

        }

        inner class QuittingState : State() {
            override fun processMessage(msg: Message?): Boolean? {
                return NOT_HANDLED
            }
        }


        override fun handleMessage(msg: Message?) {

            if (msg == null) return

            if (!mHasQuit) {
                if (mSm != null && msg.what !in arrayOf(SM_INIT_CMD, SM_QUIT_CMD)) {
                    mSm?.onPreHandleMessage(msg)
                }

                if (isDebug) Log.i(TAG, "handleMessage: E msg.what=" + msg.what)
            }

            *//** save current msg *//*
            mMsg = msg

            var msgProcessedState: State? = null

            if (mIsContructionCompleted) {
                msgProcessedState = processMsg(msg)
            } else if (!mIsContructionCompleted
                    && (msg.what == SM_INIT_CMD)
                    && (msg.obj == mSmHandlerObj)) {
                *//** Initial one time path *//*
                mIsContructionCompleted = true
                invokeEnterMethods(0)
            } else {
                throw RuntimeException("StateMachine.handlerMessage: "
                        + "The start method not call , received msg $msg")
            }

            performTransitions(msgProcessedState!!, msg)

            if (mSm == null) return

            if (isDebug) Log.i(TAG, "handleMessage: X ")

            if (msg.what !in arrayOf(SM_INIT_CMD, SM_INIT_CMD)) {
                mSm!!.onPostHanderMessage(msg)
            }
        }

        private val mLogRecords: Any? = null

        private val mQuittingState: QuittingState = QuittingState()

        private val mHaltingState: HaltingState = HaltingState()

        private fun performTransitions(msgProcessedState: State, msg: Message) {
            *//**
             * If transitionInfo has been called, exit and then enter the
             * appropriate states, We loop on this to allow enter and exit
             * methods to use transitionInfo.
             *//*
            val orgState = mStateStruck[mStateStackTopIndex].state

            *//**
             * Record whether message needs to be logged before we transition and
             * we won't log special messages SM_INIT_CMD or SM_QUIT_CMD which always
             * set msg.obj to the handler
             *//*
            var recordLogMsg = mSm?.recordLogRec(mMsg) == true && (msg.obj != mSmHandlerObj)

            if (mLogRecords.logOnlyTransitions()) {
                // add log
                if (mDestState != null) {
                    mLogRecords.add(mSm, mMsg, mSm?.getLogRecString(mMsg) ?: "", msgProcessedState,
                            orgState, mDestState)
                }
            } else if (recordLogMsg) {
                mLogRecords.add(mSm, mMsg, mSm?.getLogRecString(mMsg), msgProcessedState, orgState,
                        mDestState)
            }

            var desState = mDestState

            desState?.let {

                while (true) {
                    if (isDebug) Log.i(TAG, "handleMessage: new destination call exit/enter")

                    val commonStateInfo = setupTempStateStackWithStatesToEnter(desState);

                    mTransitionInProgress = true

                    invokeExitMethods(commonStateInfo)

                    val stateStackEnteringIndex = moveTempStateStackToStateStack();

                    invokeEnterMethods(stateStackEnteringIndex)

                    moveDeferredMessageAtFrontOfQueue()

                    if (desState != mDestState) desState = mDestState else break

                }

                mDestState = null
            }

            desState?.let {
                when (it) {
                    *//**
                     * Call onQuitting to let subclasses cleanup.
                     *//*
                    mQuittingState -> {
                        mSm?.onQuitting()

                        cleanupAfterQuitting()
                    }

                    *//**
                     * Call onHalgting() if we've transitioned to the halting state,
                     * All subsequent messages will be processed in
                     * in the halting state which invokes haltedProcessMessage(msg)
                     *//*
                    mHaltingState -> mSm?.onHalting()
                    else -> {

                    }
                }
            }

        }

        *//**
         * Cleanup all the static variables and the looper after the SM has quit
         *//*
        private fun cleanupAfterQuitting() {
            if (mSm?.mSmThread != null) {
                looper.quit()
                mSm?.mSmThread = null
            }

            mSm?.mSmHandler = null

            mSm = null
            mMsg = null
            mLogRecords?.cleanup()


        }

        private fun moveDeferredMessageAtFrontOfQueue() {

        }

        private fun invokeExitMethods(commonStateInfo: StateMachine.SmHandler.StateInfo) {

        }

        private fun moveTempStateStackToStateStack(): Int {

        }

        private fun setupTempStateStackWithStatesToEnter(desState: State): StateInfo {


        }

        private fun invokeEnterMethods(i: Int) {

        }

        private fun processMsg(msg: Message): State {

        }

        fun addState(state: State, parent: State) {

        }


    }

    private fun onHalting() {
    }

    private fun onQuitting() {

    }

    private fun getLogRecString(mMsg: Message?): Any {

    }

    private fun recordLogRec(mMsg: Message?): Boolean {
        return true
    }

    private fun onPostHanderMessage(msg: Message) {

    }

    private fun onPreHandleMessage(msg: Message?) {

    }


    companion object {

        private const val TAG = "StateMachine"
        private val isDebug: Boolean = true;

        // msg.what value when initializing
        const val SM_INIT_CMD = -1
        // msg.what value when quitting
        const val SM_QUIT_CMD = -2
        const val HANDLED: Boolean = false
        const val NOT_HANDLED: Boolean = false
    }


*/}


