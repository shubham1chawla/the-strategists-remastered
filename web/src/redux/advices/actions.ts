import { Advice } from './reducer';

export namespace AdviceActions {
  export const Types = {
    SET_ADVICES: 'SET_ADVICES',
    ADD_OR_PATCH_ADVICES: 'ADD_OR_PATCH_ADVICES',
  };

  export const setAdvices = (advices: Advice[]) => {
    return {
      type: Types.SET_ADVICES,
      payload: advices,
    };
  };

  export const addOrPatchAdvices = (advices: Advice[]) => {
    return {
      type: Types.ADD_OR_PATCH_ADVICES,
      payload: advices,
    };
  };
}
