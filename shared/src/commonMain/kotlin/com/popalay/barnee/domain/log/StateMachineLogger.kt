package com.popalay.barnee.domain.log

import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.SideEffect
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.util.Logger

class StateMachineLogger(private val logger: Logger) {
    fun <T : StateMachine<*, *, *, *>> log(tag: T, action: Action) {
        logger.info(tag::class.simpleName.orEmpty(), "action => $action")
    }

    fun <T : StateMachine<*, *, *, *>> log(tag: T, mutation: Mutation) {
        logger.info(tag::class.simpleName.orEmpty(), "mutation => $mutation")
    }

    fun <T : StateMachine<*, *, *, *>> log(tag: T, state: State) {
        logger.info(tag::class.simpleName.orEmpty(), "state => $state")
    }

    fun <T : StateMachine<*, *, *, *>> log(tag: T, sideEffect: SideEffect) {
        logger.info(tag::class.simpleName.orEmpty(), "side effect => $sideEffect")
    }
}
