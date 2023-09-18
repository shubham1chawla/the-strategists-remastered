import { Button, Modal } from 'antd';
import { State, UserActions } from '../redux';
import { useDispatch, useSelector } from 'react-redux';
import { Confetti } from './Confetti';
import { LogoutOutlined, StopFilled } from '@ant-design/icons';
import { ResetModal, Stats } from '.';
import { useState } from 'react';

export interface WinModalProps {
  open: boolean;
  onCancel: () => void;
}

export const WinModal = (props: WinModalProps) => {
  const { user, lobby } = useSelector((state: State) => state);
  const [showResetModal, setShowResetModal] = useState(false);
  const dispatch = useDispatch();

  // checking the inputs
  const { open, onCancel } = props;
  if (!open || !onCancel) {
    return null;
  }

  // determining the winner
  const player = lobby.players.find((p) => p.state === 'ACTIVE');

  return (
    <>
      <Confetti type="multiple" />
      <Modal
        open={open}
        closable={false}
        maskClosable={false}
        title="The Strategists Winner"
        footer={
          user.type === 'admin' ? (
            <>
              <Button
                type="primary"
                icon={<StopFilled />}
                onClick={() => setShowResetModal(true)}
              >
                Reset
              </Button>
            </>
          ) : (
            <Button
              type="primary"
              icon={<LogoutOutlined />}
              onClick={() => dispatch(UserActions.unsetUser())}
            >
              Logout
            </Button>
          )
        }
      >
        <Stats player={player} />
      </Modal>
      {showResetModal ? (
        <ResetModal
          open={showResetModal}
          onCancel={() => setShowResetModal(false)}
        />
      ) : null}
    </>
  );
};
